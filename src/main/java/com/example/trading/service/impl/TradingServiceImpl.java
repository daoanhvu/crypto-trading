package com.example.trading.service.impl;

import com.example.trading.domain.repository.PriceRepository;
import com.example.trading.dto.PriceDTO;
import com.example.trading.service.TradingService;
import com.example.trading.util.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradingServiceImpl implements TradingService {

    private final PriceRepository priceRepository;

    @Autowired
    public TradingServiceImpl(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @Override
    public PriceDTO retrieveBestPrice(String symbol) {
        return priceRepository.findById(symbol).map(DTOMapper::toPriceEntity).orElse(null);
    }
}
