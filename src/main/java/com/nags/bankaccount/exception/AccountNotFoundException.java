package com.nags.bankaccount.exception;

public class AccountNotFoundException extends IllegalArgumentException {
    public AccountNotFoundException(String id) {
        super("Account with id " + id + " not found.");
    }

}
