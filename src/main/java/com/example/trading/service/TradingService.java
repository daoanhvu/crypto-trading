package com.example.trading.service;

import com.example.trading.dto.ApiResponse;
import com.example.trading.dto.PageableResponse;
import com.example.trading.dto.PriceDTO;
import com.example.trading.dto.TransactionDTO;
import org.springframework.data.domain.Pageable;

public interface TradingService {

    PriceDTO retrieveBestPrice(String symbol);

    ApiResponse<Long> doTransaction(TransactionDTO trans);
    PageableResponse<TransactionDTO> getTransactionsByUser(String username, Pageable pageable);
}
