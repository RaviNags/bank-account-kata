package com.nags.bankaccount.service;


import com.nags.bankaccount.exception.AccountNotFoundException;
import com.nags.bankaccount.exception.OverdraftException;
import com.nags.bankaccount.reference.Operation;
import com.nags.bankaccount.reference.Transaction;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

        Instant expectedDate = Instant.now();
        var mock = mockStatic(Instant.class);
        when(Instant.now()).thenReturn(expectedDate);
        // fist deposit
        BigDecimal balance = this.accountService.deposit(id, new BigDecimal(500));
        assertEquals(0, balance.compareTo(new BigDecimal(500)));
        List<Transaction> transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 1);
        assertEquals(transactions.get(0).operation(), Operation.DEPOSIT);
        assertEquals(0, transactions.get(0).balance().compareTo(balance));
        assertEquals(0, transactions.get(0).amount().compareTo(new BigDecimal(500)));
        assertEquals(expectedDate, transactions.get(0).date());
        mock.closeOnDemand();

        Instant expectedDate2 = Instant.now();
        mock = mockStatic(Instant.class);
        when(Instant.now()).thenReturn(expectedDate2);
        // second deposit
        balance = this.accountService.deposit(id, new BigDecimal(1500));
        assertEquals(0, balance.compareTo(new BigDecimal(2000)));
        transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 2);
        assertEquals(transactions.get(1).operation(), Operation.DEPOSIT);
        assertEquals(0, transactions.get(1).balance().compareTo(balance));
        assertEquals(0, transactions.get(1).amount().compareTo(new BigDecimal(1500)));
        assertEquals(expectedDate2, transactions.get(1).date());
        mock.closeOnDemand();
    }

    @Test
    @DisplayName("Deposit work with absolute value only test")
    void depositAbsolutValueTest() {
        String id = this.accountService.createAccount();

        Instant expectedDate = Instant.now();
        var mock = mockStatic(Instant.class);
        when(Instant.now()).thenReturn(expectedDate);

        BigDecimal balance = this.accountService.deposit(id, new BigDecimal(-500));
        assertEquals(0, balance.compareTo(new BigDecimal(500)));
        List<Transaction> transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 1);
        assertEquals(transactions.get(0).operation(), Operation.DEPOSIT);
        assertEquals(0, transactions.get(0).balance().compareTo(balance));
        assertEquals(0, transactions.get(0).amount().compareTo(new BigDecimal(500)));
        assertEquals(expectedDate, transactions.get(0).date());
        mock.closeOnDemand();
    }

    @Test
    @DisplayName("Withdrawal working test")
    void withdrawalTest() {
        String id = this.accountService.createAccount();
        // fist deposit for test
        BigDecimal balance = this.accountService.deposit(id, new BigDecimal(2000));
        assertEquals(0, balance.compareTo(new BigDecimal(2000)));

        // first withdrawal
        Instant expectedDate = Instant.now();
        var mock = mockStatic(Instant.class);
        when(Instant.now()).thenReturn(expectedDate);
        balance = this.accountService.withdrawal(id, new BigDecimal(200));
        assertEquals(0, balance.compareTo(new BigDecimal(1800)));
        List<Transaction> transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 2);
        assertEquals(transactions.get(1).operation(), Operation.WITHDRAWAL);
        assertEquals(0, transactions.get(1).balance().compareTo(balance));
        assertEquals(0, transactions.get(1).amount().compareTo(new BigDecimal(200)));
        assertEquals(expectedDate, transactions.get(1).date());
        mock.closeOnDemand();

        // second withdrawal
        Instant expectedDate2 = Instant.now();
        mock = mockStatic(Instant.class);
        when(Instant.now()).thenReturn(expectedDate2);
        balance = this.accountService.withdrawal(id, new BigDecimal("1000.50"));
        assertEquals(0, balance.compareTo(new BigDecimal("799.50")));
        transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 3);
        assertEquals(transactions.get(2).operation(), Operation.WITHDRAWAL);
        assertEquals(0, transactions.get(2).balance().compareTo(balance));
        assertEquals(0, transactions.get(2).amount().compareTo(new BigDecimal("1000.50")));
        assertEquals(expectedDate2, transactions.get(2).date());
        mock.closeOnDemand();
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

        Instant expectedDate = Instant.now();
        var mock = mockStatic(Instant.class);
        when(Instant.now()).thenReturn(expectedDate);
        balance = this.accountService.withdrawal(id, new BigDecimal(-500));
        assertEquals(0, balance.compareTo(new BigDecimal(0)));
        List<Transaction> transactions = this.accountService.history(id);
        assertEquals(transactions.size(), 2);
        assertEquals(transactions.get(1).operation(), Operation.WITHDRAWAL);
        assertEquals(0, transactions.get(1).balance().compareTo(balance));
        assertEquals(0, transactions.get(1).amount().compareTo(new BigDecimal(500)));
        assertEquals(expectedDate, transactions.get(1).date());
        mock.closeOnDemand();
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
