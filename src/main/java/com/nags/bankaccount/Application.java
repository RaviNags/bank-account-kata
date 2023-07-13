package com.nags.bankaccount;

import com.nags.bankaccount.reference.Operation;
import com.nags.bankaccount.reference.Transaction;
import com.nags.bankaccount.service.AccountService;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;

public class Application {

    public static void main(String[] args) throws AccountNotFoundException {
        AccountService accountService = AccountService.getInstance();

        String accountId = accountService.createAccount();
        accountService.deposit(accountId, new BigDecimal(2000));
        accountService.withdrawal(accountId, new BigDecimal(1500));
        printHistory(accountService.history(accountId));
    }

    private static void printHistory(List<Transaction> transactions) {
        transactions.forEach(transaction ->
                System.out.println(
                        transaction.date() + ": "
                        + (Operation.WITHDRAWAL.equals(transaction.operation()) ? "-" : "")
                        + transaction.amount()
                        + ", balance : " + transaction.balance()));
    }
}
