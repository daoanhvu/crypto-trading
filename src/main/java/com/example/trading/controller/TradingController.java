package com.example.trading.controller;

import com.example.trading.dto.ApiResponse;
import com.example.trading.dto.PageableResponse;
import com.example.trading.dto.PriceDTO;
import com.example.trading.dto.TransactionDTO;
import com.example.trading.dto.WalletDTO;
import com.example.trading.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/trading")
public class TradingController {

    private final TradingService tradingService;

    @Autowired
    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @RequestMapping(path = "/prices/{symbol}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPriceBySymbol(@PathVariable("symbol") String symbol) {
        ApiResponse<PriceDTO> response = tradingService.retrieveBestPrice(symbol);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    @RequestMapping(path = "/wallets/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> retrieveWallet(@PathVariable("username") String username) {
        ApiResponse<WalletDTO> response = tradingService.retrieveWallet(username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    @RequestMapping(path = "/transactions", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> doTransaction(@RequestBody TransactionDTO trans) {
        ApiResponse<Long> response = tradingService.doTransaction(trans);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getHttpStatusCode()));
    }

    @RequestMapping(path = "/transactions", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTransactionsByUser(@RequestParam("username") String username, Pageable pageable) {
        PageableResponse<TransactionDTO> response = tradingService.getTransactionsByUser(username, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
