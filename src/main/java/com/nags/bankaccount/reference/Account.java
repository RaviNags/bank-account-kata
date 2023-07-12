package com.nags.bankaccount.reference;

import com.nags.bankaccount.exception.OverdraftException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private BigDecimal balance;
    private List<Transaction> transactions;

    public Account() {
        this.balance = new BigDecimal(0);
        this.transactions = new ArrayList<>();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
