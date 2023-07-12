package com.nags.bankaccount.exception;

import com.nags.bankaccount.reference.Account;

import java.math.BigDecimal;

public class OverdraftException extends IllegalArgumentException {

    public OverdraftException(BigDecimal balance) {
        super("Cannot execute the transaction because the new balance " + balance + " exceeds the overdraft.");
    }
}
