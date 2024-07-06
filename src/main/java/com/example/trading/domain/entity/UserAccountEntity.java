package com.example.trading.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_accounts")
public class UserAccountEntity {
    @Id
    private String username;

    // Balance in USDT
    private double balance;
}
