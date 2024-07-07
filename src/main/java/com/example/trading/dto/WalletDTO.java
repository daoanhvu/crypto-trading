package com.example.trading.dto;

public class WalletDTO {
    private String username;
    private double balance;

    public WalletDTO() { }

    public WalletDTO(String username, double bal) {
        this.username = username;
        this.balance = bal;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
