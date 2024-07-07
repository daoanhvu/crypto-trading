package com.example.trading.dto;

import java.time.OffsetDateTime;

public class TransactionDTO {
    private Long id;
    private String fromUser;
    private String toUser;
    private double amount;

    private OffsetDateTime transactionTime;

    public TransactionDTO() { }

    public TransactionDTO(Long id, String fromUser, String toUser, double amount, OffsetDateTime transTime) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.transactionTime = transTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public OffsetDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(OffsetDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}
