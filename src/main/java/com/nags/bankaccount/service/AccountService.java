package com.nags.bankaccount.service;

import com.nags.bankaccount.exception.AccountNotFoundException;
import com.nags.bankaccount.exception.OverdraftException;
import com.nags.bankaccount.reference.Account;
import com.nags.bankaccount.reference.Operation;
import com.nags.bankaccount.reference.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {

    private final BigDecimal OVERDRAFT = new BigDecimal(0);
    private static AccountService instance;
    private final Map<String, Account> accounts;

    private AccountService() {
        accounts = new ConcurrentHashMap<>();
    }

    public synchronized static AccountService getInstance() {
        if (instance == null) {
            instance = new AccountService();
        }
        return instance;
    }

    public String createAccount() {
        String id = UUID.randomUUID().toString();
        accounts.put(id, new Account());
        return id;
    }

    /***
     * Make a deposit of the specified amount on the user account
     * @param account id
     * @param amount
     * @throws AccountNotFoundException
     */
    public BigDecimal deposit(String id, BigDecimal amount) throws AccountNotFoundException {
        Account account = this.accounts.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        // we take the absolute value to handle a negative value error
        // business choice, can be handled in a different way
        amount = amount.abs();
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        account.getTransactions().add(new Transaction(Operation.DEPOSIT, Instant.now(), amount, newBalance));
        return newBalance;
    }

    /***
     * Make a withdrawal of the specified amount on the user account
     * @param id
     * @param amount
     * @throws AccountNotFoundException
     */
    public BigDecimal withdrawal(String id, BigDecimal amount) throws AccountNotFoundException {
        Account account = this.accounts.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        amount = amount.abs(); // we take the absolute value to handle a negative value error
        // business choice, can be handled in a different way
        BigDecimal newBalance = account.getBalance().subtract(amount);
        if (newBalance.compareTo(OVERDRAFT) < 0) {
            throw new OverdraftException(newBalance);
        }
        account.setBalance(newBalance);
        account.getTransactions().add(new Transaction(Operation.WITHDRAWAL, Instant.now(), amount, newBalance));
        return newBalance;
    }

    /***
     * Give the list of transactions executed on the specified account
     * @param id
     * @return
     * @throws AccountNotFoundException
     */
    public List<Transaction> history(String id) throws AccountNotFoundException {
        Account account = this.accounts.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return account.getTransactions();
    }
}
