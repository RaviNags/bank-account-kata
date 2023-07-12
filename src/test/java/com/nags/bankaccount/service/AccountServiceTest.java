package com.nags.bankaccount.service;


import com.nags.bankaccount.exception.AccountNotFoundException;
import com.nags.bankaccount.exception.OverdraftException;
import com.nags.bankaccount.reference.Operation;
import com.nags.bankaccount.reference.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountServiceTest {

    AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = AccountService.getInstance();
    }

    @Test
    @DisplayName("Deposit throws exception when account not exist")
    void depositAccountNotFoundExceptionTest() {
        AccountNotFoundException thrown = Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.accountService.deposit("FAKE_ID", new BigDecimal(500));
        });
        assertEquals("Account with id FAKE_ID not found.", thrown.getMessage());
    }

    @Test
    @DisplayName("Deposit working test")
    void depositTest() {
        String id = this.accountService.createAccount();
        // fist deposit
        BigDecimal balance = this.accountService.deposit(id, new BigDecimal(500));
        assertEquals(0, balance.compareTo(new BigDecimal(500)));
        List<Transaction> transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 1);
        assertEquals(transactions.get(0).operation(), Operation.DEPOSIT);
        assertEquals(0, transactions.get(0).balance().compareTo(balance));
        assertEquals(0, transactions.get(0).amount().compareTo(new BigDecimal(500)));

        // second deposit
        balance = this.accountService.deposit(id, new BigDecimal(1500));
        assertEquals(0, balance.compareTo(new BigDecimal(2000)));
        transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 2);
        assertEquals(transactions.get(1).operation(), Operation.DEPOSIT);
        assertEquals(0, transactions.get(1).balance().compareTo(balance));
        assertEquals(0, transactions.get(1).amount().compareTo(new BigDecimal(1500)));
    }

    @Test
    @DisplayName("Deposit work with absolute value only test")
    void depositAbsolutValueTest() {
        String id = this.accountService.createAccount();

        BigDecimal balance = this.accountService.deposit(id, new BigDecimal(-500));
        assertEquals(0, balance.compareTo(new BigDecimal(500)));
        List<Transaction> transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 1);
        assertEquals(transactions.get(0).operation(), Operation.DEPOSIT);
        assertEquals(0, transactions.get(0).balance().compareTo(balance));
        assertEquals(0, transactions.get(0).amount().compareTo(new BigDecimal(500)));
    }

    @Test
    @DisplayName("Withdrawal working test")
    void withdrawalTest() {
        String id = this.accountService.createAccount();
        // fist deposit for test
        BigDecimal balance = this.accountService.deposit(id, new BigDecimal(2000));
        assertEquals(0, balance.compareTo(new BigDecimal(2000)));

        // first withdrawal
        balance = this.accountService.withdrawal(id, new BigDecimal(200));
        assertEquals(0, balance.compareTo(new BigDecimal(1800)));
        List<Transaction> transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 2);
        assertEquals(transactions.get(1).operation(), Operation.WITHDRAWAL);
        assertEquals(0, transactions.get(1).balance().compareTo(balance));
        assertEquals(0, transactions.get(1).amount().compareTo(new BigDecimal(200)));

        // second withdrawal
        balance = this.accountService.withdrawal(id, new BigDecimal("1000.50"));
        assertEquals(0, balance.compareTo(new BigDecimal("799.50")));
        transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 3);
        assertEquals(transactions.get(2).operation(), Operation.WITHDRAWAL);
        assertEquals(0, transactions.get(2).balance().compareTo(balance));
        assertEquals(0, transactions.get(2).amount().compareTo(new BigDecimal("1000.50")));
    }

    @Test
    @DisplayName("Withdrawal throws exception when the overdraft value is exceed")
    void withdrawalOverdraftException() {
        String id = this.accountService.createAccount();
        OverdraftException thrown = Assertions.assertThrows(OverdraftException.class, () -> {
            this.accountService.withdrawal(id, new BigDecimal(500));
        });
        assertEquals("Cannot execute the transaction because the new balance -500 exceeds the overdraft.", thrown.getMessage());
    }

    @Test
    @DisplayName("Withdrawal work with absolute value only test")
    void withdrawalAbsolutValueTest() {
        String id = this.accountService.createAccount();
        // fist deposit for test
        BigDecimal balance = this.accountService.deposit(id, new BigDecimal(500));

        balance = this.accountService.withdrawal(id, new BigDecimal(-500));
        assertEquals(0, balance.compareTo(new BigDecimal(0)));
        List<Transaction> transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 2);
        assertEquals(transactions.get(1).operation(), Operation.WITHDRAWAL);
        assertEquals(0, transactions.get(1).balance().compareTo(balance));
        assertEquals(0, transactions.get(1).amount().compareTo(new BigDecimal(500)));
    }

    @Test
    @DisplayName("Withdrawal throws exception when account not exist")
    void withdrawalAccountNotFoundException() {
        AccountNotFoundException thrown = Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.accountService.withdrawal("FAKE_ID", new BigDecimal(500));
        });
        assertEquals("Account with id FAKE_ID not found.", thrown.getMessage());
    }

    @Test
    @DisplayName("History throws exception when account not exist")
    void historyAccountNotFoundException() {
        AccountNotFoundException thrown = Assertions.assertThrows(AccountNotFoundException.class, () -> {
            this.accountService.history("FAKE_ID");
        });
        assertEquals("Account with id FAKE_ID not found.", thrown.getMessage());
    }

}
