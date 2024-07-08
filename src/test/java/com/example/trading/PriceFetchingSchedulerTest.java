package com.example.trading;

import com.example.trading.domain.repository.PriceRepository;
import com.example.trading.dto.PriceDTO;
import com.example.trading.service.PriceFetchingScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class PriceFetchingSchedulerTest {

	@Mock
	private PriceRepository priceRepository;
	@Mock
	private RestTemplate restTemplate;

	@Test
	public void shouldAggregatePricesSuccessfully() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		PriceFetchingScheduler scheduler = new PriceFetchingScheduler(restTemplate, "http://fake.binanceUrl",
				"http://fake.huobi.url", priceRepository);

		// Given
		PriceDTO priceDTO1 = new PriceDTO("ETHUSDT", 1.0, 5, 1.5, 3);
		PriceDTO priceDTO2 = new PriceDTO("BTCUSDT", 0.5, 10, 0.7, 5);
		List<PriceDTO> binanceList = new ArrayList<>();
		binanceList.add(priceDTO1);
		binanceList.add(priceDTO2);

		PriceDTO priceDTO3 = new PriceDTO("ETHUSDT", 0.9, 5, 1.7, 3);
		PriceDTO priceDTO4 = new PriceDTO("BTCUSDT", 0.6, 10, 0.7, 5);
		List<PriceDTO> huobiList = new ArrayList<>();
		huobiList.add(priceDTO3);
		huobiList.add(priceDTO4);

		// When
		Method method = scheduler.getClass().getDeclaredMethod("aggregatePrices", List.class, List.class);
		method.setAccessible(true);
		List<PriceDTO> bestPrices = (List<PriceDTO>) method.invoke(scheduler, new Object[] {binanceList, huobiList});

		// Then
		Assertions.assertNotNull(bestPrices);
		Assertions.assertEquals(2, bestPrices.size());
		double bidPriceETHUSDT = bestPrices.stream()
				.filter(price -> Objects.equals(price.getSymbol(), "ETHUSDT"))
				.findAny().map(PriceDTO::getBidPrice)
				.orElse(-1.0);
		Assertions.assertEquals(1.0, bidPriceETHUSDT, 0.00001);
		double bidPriceBTCUSDT = bestPrices.stream()
				.filter(price -> Objects.equals(price.getSymbol(), "BTCUSDT"))
				.findAny().map(PriceDTO::getBidPrice)
				.orElse(-1.0);
		Assertions.assertEquals(0.6, bidPriceBTCUSDT, 0.00001);
	}

}
