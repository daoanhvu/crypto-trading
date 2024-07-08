package com.example.trading.service.impl;

import com.example.trading.domain.entity.TransactionEntity;
import com.example.trading.domain.entity.UserAccountEntity;
import com.example.trading.domain.repository.PriceRepository;
import com.example.trading.domain.repository.TransactionRepository;
import com.example.trading.domain.repository.UserAccountRepository;
import com.example.trading.dto.ApiResponse;
import com.example.trading.dto.PageableResponse;
import com.example.trading.dto.PriceDTO;
import com.example.trading.dto.TransactionDTO;
import com.example.trading.dto.WalletDTO;
import com.example.trading.service.TradingService;
import com.example.trading.util.DTOMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
public class TradingServiceImpl implements TradingService {
    private final Logger LOG = LoggerFactory.getLogger(TradingServiceImpl.class);

    private final PriceRepository priceRepository;
    private final UserAccountRepository userAccountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TradingServiceImpl(PriceRepository priceRepository,
                              UserAccountRepository userAccountRepository,
                              TransactionRepository transactionRepository) {
        this.priceRepository = priceRepository;
        this.userAccountRepository = userAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostConstruct
    public void initTestData() {

        UserAccountEntity user1 = new UserAccountEntity();
        user1.setUsername("user1");
        user1.setBalance(5000.0);

        UserAccountEntity user2 = new UserAccountEntity();
        user2.setUsername("user2");
        user2.setBalance(5000.0);

        userAccountRepository.saveAll(Arrays.asList(user1,user2));
    }

    @Override
    public ApiResponse<PriceDTO> retrieveBestPrice(String symbol) {
        return priceRepository
                .findById(symbol)
                .map(price -> new ApiResponse<>(null, 200, DTOMapper.toPriceDTO(price)))
                .orElseGet(() -> new ApiResponse<>("Price for " + symbol + " not found.", 404, null));
    }

    @Override
    @Transactional
    public ApiResponse<Long> doTransaction(TransactionDTO trans) {
        ApiResponse<Long> response = new ApiResponse<>();
        Optional<UserAccountEntity> senderOpt = userAccountRepository.findById(trans.getFromUser());
        if (senderOpt.isEmpty()) {
            response.setHttpStatusCode(404);
            response.setMessage("Sender info not found.");
            return response;
        }

        Optional<UserAccountEntity> receiverOpt = userAccountRepository.findById(trans.getToUser());
        if (receiverOpt.isEmpty()) {
            response.setHttpStatusCode(404);
            response.setMessage("Receiver info not found.");
            return response;
        }

        UserAccountEntity sender = senderOpt.get();
        UserAccountEntity receiver = receiverOpt.get();
        if (sender.getBalance() < trans.getAmount()) {
            response.setHttpStatusCode(400);
            response.setMessage("Balance not enough to do this transaction");
            return response;
        }

        try {
            TransactionEntity transactionEntity = new TransactionEntity(
                    null,
                    sender.getUsername(),
                    receiver.getUsername(),
                    trans.getAmount(),
                    OffsetDateTime.now(Clock.systemUTC())
            );
            sender.setBalance(sender.getBalance() - trans.getAmount());
            receiver.setBalance(receiver.getBalance() + trans.getAmount());
            userAccountRepository.saveAll(Arrays.asList(sender, receiver));
            TransactionEntity savedTrans = transactionRepository.save(transactionEntity);

            response.setResult(savedTrans.getId());
            LOG.debug("Transaction from {} to {} with amount {} has been done successfully", sender.getUsername(), receiver.getUsername(), trans.getAmount());
            return response;
        } catch (Exception e) {
            LOG.error("Error occur while executing a transaction with input {}", trans, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageableResponse<TransactionDTO> getTransactionsByUser(String username, Pageable pageable) {
        Page<TransactionDTO> pageResult = transactionRepository.retrieveTransactionsByUser(username, pageable)
                .map(DTOMapper::toTransactionDTO);
//        Page<TransactionDTO> pageResult = transactionRepository.findAll(pageable).map(DTOMapper::toTransactionDTO);
        PageableResponse<TransactionDTO> response = new PageableResponse<>();
        response.setNumberOfElements(pageResult.getNumberOfElements());
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());
        response.setPage(pageable.getPageNumber());
        response.setSize(pageable.getPageSize());
        response.setContent(pageResult.getContent());
        return response;
    }

    @Override
    public ApiResponse<WalletDTO> retrieveWallet(String username) {
        ApiResponse<WalletDTO> response = new ApiResponse<>();
        Optional<UserAccountEntity> senderOpt = userAccountRepository.findById(username);
        if (senderOpt.isEmpty()) {
            response.setHttpStatusCode(404);
            response.setMessage("User info not found.");
            return response;
        }

        response.setHttpStatusCode(200);
        response.setResult(DTOMapper.toWalletDTO(senderOpt.get()));
        return response;
    }
}
