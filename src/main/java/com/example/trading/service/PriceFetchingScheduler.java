package com.example.trading.service;

import com.example.trading.domain.repository.PriceRepository;
import com.example.trading.dto.BinancePriceDTO;
import com.example.trading.dto.HuobiPriceDTO;
import com.example.trading.dto.HuobiPriceResponseDTO;
import com.example.trading.dto.PriceDTO;
import com.example.trading.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PriceFetchingScheduler {
    private final PriceRepository priceRepository;
    private final String binanceUrl;
    private final String huobiUrl;
    private final RestTemplate restTemplate;

    private final ParameterizedTypeReference<List<BinancePriceDTO>> BINANCE_DTO_TYPE_REF = new ParameterizedTypeReference<>(){};
    private final ExecutorService fixedExecutor = Executors.newFixedThreadPool(2);
    private final AtomicBoolean fetchingInProgress = new AtomicBoolean(false);
    private final List<String> SUPPORT_SYMBOLS = Arrays.asList("ETHUSDT", "BTCUSDT");

    @Autowired
    public PriceFetchingScheduler(RestTemplate restTemplate,
            @Value("${priceFetching.binanceUrl}") String binanceUrl,
                                  @Value("${priceFetching.huobiUrl}") String huobiUrl,
                                  PriceRepository priceRepository) {
        this.binanceUrl = binanceUrl;
        this.huobiUrl = huobiUrl;
        this.restTemplate = restTemplate;
        this.priceRepository = priceRepository;
    }

    @Scheduled(fixedRateString = "${priceFetching.timeRateInMs}")
    public void performPriceFetching() {
        if (fetchingInProgress.getAndSet(true)) {
            try {
                Future<List<PriceDTO>> binanceTask = fixedExecutor.submit(() -> {
                    ResponseEntity<List<BinancePriceDTO>> binanceResponse = restTemplate.exchange(binanceUrl,
                            HttpMethod.GET,
                            null,
                            BINANCE_DTO_TYPE_REF);
                    if (binanceResponse.getStatusCode().is2xxSuccessful()) {
                        List<BinancePriceDTO> binancePrices = binanceResponse.getBody();
                        if (binancePrices != null) {
                            return binancePrices.stream()
                                    .filter(itm -> SUPPORT_SYMBOLS.contains(itm.getSymbol().toUpperCase()))
                                    .map(DTOMapper::toPriceDTO).collect(Collectors.toList());
                        }
                    }
                    return Collections.emptyList();
                });

                Future<List<PriceDTO>> huobiTask = fixedExecutor.submit(() -> {
                    ResponseEntity<HuobiPriceResponseDTO> huobiResponse = restTemplate.exchange(huobiUrl,
                            HttpMethod.GET,
                            null,
                            HuobiPriceResponseDTO.class);
                    if (huobiResponse.getStatusCode().is2xxSuccessful() && Objects.nonNull(huobiResponse.getBody())) {
                        List<HuobiPriceDTO> prices = huobiResponse.getBody().getData();
                        if (prices != null) {
                            return prices.stream()
                                    .filter(itm -> SUPPORT_SYMBOLS.contains(itm.getSymbol()))
                                    .map(DTOMapper::toPriceDTO).collect(Collectors.toList());
                        }
                    }
                    return Collections.emptyList();
                });

                List<PriceDTO> binanceList = binanceTask.get();
                List<PriceDTO> huobiList = huobiTask.get();
                aggregatePrices(binanceList, huobiList);

            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                fetchingInProgress.set(false);
            }
        }
    }

    private void aggregatePrices(List<PriceDTO> binanceList, List<PriceDTO> huobiList) {
        Map<String, PriceDTO> binancePriceMap = binanceList
                .stream()
                .collect(Collectors.toMap(PriceDTO::getSymbol, Function.identity()));

        List<PriceDTO> bestPrices = new ArrayList<>();
        for (PriceDTO hItem: huobiList) {
            if (binancePriceMap.containsKey(hItem.getSymbol())) {
                PriceDTO bItem = binancePriceMap.get(hItem.getSymbol());
                // TODO: I don't how to calculate the best price so I do it the following way
                PriceDTO bestItem = new PriceDTO(bItem.getSymbol(),
                        Math.max(hItem.getBidPrice(), bItem.getBidPrice()),
                        bItem.getBidQty() + hItem.getBidQty(),
                        Math.min(hItem.getAskPrice(), bItem.getAskPrice()),
                        bItem.getAskQty() + hItem.getAskQty());
                bestPrices.add(bestItem);
                // Remove the calculated from binance list
                binanceList.remove(bItem);
            } else {
                // Binance doesn't have this symbol, let pick the one from Huobi
                bestPrices.add(hItem);
            }
        }
        if (!binanceList.isEmpty()) {
            bestPrices.addAll(binanceList);
        }

        priceRepository.saveAllAndFlush(bestPrices.stream().map(DTOMapper::toPriceEntity).collect(Collectors.toList()));
    }
}
