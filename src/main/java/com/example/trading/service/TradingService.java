package com.example.trading.service;

import com.example.trading.dto.PriceDTO;

public interface TradingService {

    PriceDTO retrieveBestPrice(String symbol);
}
