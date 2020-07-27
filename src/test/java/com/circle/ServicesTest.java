package com.circle;

import com.circle.jobs.ProcessTransactionsJob;
import com.circle.models.Account;
import com.circle.models.Transaction;
import com.circle.services.AccountService;
import com.circle.services.TransactionService;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.ds.PGSimpleDataSource;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicesTest {

    private static AccountService accountService = null;
    private static TransactionService transactionService = null;
    private static ProcessTransactionsJob processTransactionsJob = null;

    public ServicesTest() {
        DataSource dataSource = initDataSource();
        DBI dbi = new DBI(dataSource);

        accountService = AccountService.getInstance(dbi);
        transactionService = TransactionService.getInstance(dbi);
        processTransactionsJob = new ProcessTransactionsJob(dbi);

        initTestData();
    }

    private void initTestData() {
        initAccounts();
    }

    private Account testAccount;
    private Account testAccount2;
    private Transaction testTransaction;
    private Transaction testTransaction2;

    private void initAccounts() {
        testAccount = new Account();
        testAccount.setName("Test Account");
        testAccount.setEmail("test@email.com");
        testAccount.setBalance(10000);

        testAccount2 = new Account();
        testAccount2.setName("Test Account 2");
        testAccount2.setEmail("test2@email.com");
        testAccount2.setBalance(10000);

        // Initializing transactions with entire balance as transaction amount.
        testTransaction = new Transaction();
        testTransaction.setSender_id(testAccount.getId());
        testTransaction.setReceiver_id(testAccount2.getId());
        testTransaction.setAmount(testAccount.getBalance());

        testTransaction2 = new Transaction();
        testTransaction2.setSender_id(testAccount.getId());
        testTransaction2.setReceiver_id(testAccount2.getId());
        testTransaction2.setAmount(testAccount.getBalance());
    }

    private DataSource initDataSource() {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerName("localhost");
        source.setDatabaseName("test");
        source.setUser("interview_dbuser");
        source.setPassword("pass");

        return source;
    }

    @Test
    @Order(1)
    public void testAccountService() {
        // Creating account and testing its occurrence.
        accountService.addAccount(testAccount);
        Account savedAccount = accountService.getAccount(testAccount.getId());
        assertEquals(savedAccount, testAccount);

        // Testing get by invalid ID
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.getAccount(UUID.randomUUID());
        });

        // Testing getting account info by email
        Account emailAccount = accountService.getAccountByEmail(testAccount.getEmail());
        assertEquals(emailAccount, testAccount);

        // Testing updating account info
        double newBalance = savedAccount.getBalance() + 100;
        savedAccount.setBalance(newBalance);
        accountService.update(savedAccount);
        Account updatedAccount = accountService.getAccount(testAccount.getId());
        assertEquals(updatedAccount.getBalance(), newBalance);

        // Clearing the table for it to be consistent
        accountService.clearTable();
    }

    @Test
    @Order(2)
    public void testTransactionService() {
        // Initializing accounts
        accountService.addAccount(testAccount);
        accountService.addAccount(testAccount2);

        // Creating new transaction
        transactionService.addTransaction(testTransaction);

        // Testing creating and getting transaction
        Transaction savedTransaction = transactionService.getTransaction(testTransaction.getId());
        assertEquals(savedTransaction, testTransaction);

        // Updating transaction status
        savedTransaction.setStatus(Transaction.Status.FAILED);
        transactionService.update(savedTransaction);
        Transaction updatedTransaction = transactionService.getTransaction(testTransaction.getId());
        assertEquals(updatedTransaction, savedTransaction);

        // Clearing the table for it to be consistent
        accountService.clearTable();
    }

    @Test
    @Order(3)
    public void testDoubleSpends() {
        // Initializing accounts
        accountService.addAccount(testAccount);
        accountService.addAccount(testAccount2);

        // Creating multiple transactions for double spends
        // The transaction amount is set to the balance of testAccount
        transactionService.addTransaction(testTransaction);
        transactionService.addTransaction(testTransaction2);

        // Running the background job
        processTransactionsJob.run();

        // Testing that only one of the transactions should have been in DONE state and the other in FAILED.
        Transaction savedTransaction = transactionService.getTransaction(testTransaction.getId());
        Transaction savedTransaction2 = transactionService.getTransaction(testTransaction2.getId());

        // The first transaction is successful since testAccount has enough balance.
        assertEquals(savedTransaction.getStatus(), Transaction.Status.DONE);
        // The second transaction fails since the testAccount does not have enough balance.
        assertEquals(savedTransaction2.getStatus(), Transaction.Status.FAILED);

        // Clearing tables for consistency
        accountService.clearTable();
    }

    @AfterAll
    public static void clearTables() {
        System.out.println("Emptying tables");
        accountService.clearTable();
    }
}
