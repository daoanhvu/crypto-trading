package com.example.trading.util;

import com.example.trading.domain.entity.PriceEntity;
import com.example.trading.domain.entity.TransactionEntity;
import com.example.trading.domain.entity.UserAccountEntity;
import com.example.trading.dto.BinancePriceDTO;
import com.example.trading.dto.HuobiPriceDTO;
import com.example.trading.dto.PriceDTO;
import com.example.trading.dto.TransactionDTO;
import com.example.trading.dto.WalletDTO;
import org.springframework.lang.NonNull;

public final class DTOMapper {

    public static PriceDTO toPriceDTO(@NonNull BinancePriceDTO bpDto) {
        return new PriceDTO(
                bpDto.getSymbol().toUpperCase(),
                bpDto.getBidPrice(),
                bpDto.getBidQty(),
                bpDto.getAskPrice(),
                bpDto.getAskQty());
    }

    public static PriceDTO toPriceDTO(@NonNull HuobiPriceDTO hpDto) {
        return new PriceDTO(
                hpDto.getSymbol().toUpperCase(),
                hpDto.getBidPrice(),
                hpDto.getBidSize(),
                hpDto.getAskPrice(),
                hpDto.getAskSize());
    }

    public static PriceDTO toPriceDTO(@NonNull PriceEntity entity) {
        return new PriceDTO(
                entity.getSymbol().toUpperCase(),
                entity.getBidPrice(),
                entity.getBidQty(),
                entity.getAskPrice(),
                entity.getAskQty());
    }

    public static PriceEntity toPriceDTO(@NonNull PriceDTO dto) {
        return new PriceEntity(
                dto.getSymbol().toUpperCase(),
                dto.getBidPrice(),
                dto.getBidQty(),
                dto.getAskPrice(),
                dto.getAskQty());
    }

    public static TransactionDTO toTransactionDTO(@NonNull TransactionEntity entity) {
        return new TransactionDTO(
                entity.getId(),
                entity.getFromUser(),
                entity.getToUser(),
                entity.getAmount(),
                entity.getTransactionTime());
    }

    public static WalletDTO toWalletDTO(@NonNull UserAccountEntity userEntity) {
        return new WalletDTO(
                userEntity.getUsername(),
                userEntity.getBalance());
    }
}
