package com.circle.jobs;

import com.circle.exceptions.InsufficientBalanceException;
import com.circle.models.Account;
import com.circle.models.Transaction;
import com.circle.services.AccountService;
import com.circle.services.TransactionService;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This job processes all the transactions present in the transactions table.
 * Gets rows ordered by created_at and processes them one by one, thus avoiding double spending.
 */
public class ProcessTransactionsJob implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(ProcessTransactionsJob.class);

    private final AccountService accountService;
    private final TransactionService transactionService;

//    private List<Transaction> transactions;

    public ProcessTransactionsJob(DBI dbi) {
        accountService = AccountService.getInstance(dbi);
        transactionService = TransactionService.getInstance(dbi);
    }

    @Override
    public void run() {
        logger.info("Starting job:: ProcessTransactionsJob");
        List<Transaction> transactions = loadTransactions();
        for (Transaction transaction : transactions) {
            processTransactions(transaction);
        }
        logger.info("Done job:: ProcessTransactionsJob");
    }

    /**
     * This method loads transactions that are in pending state.
     * @return a list of transactions in pending state.
     */
    private List<Transaction> loadTransactions() {
        List<Transaction> transactions = transactionService.getTransactionsByStatus(Transaction.Status.PENDING);
        return transactions;
    }

    /**
     * Process each transaction
     * deduct money from the sender and add to receiver
     * update status of the transaction
     * @param transaction
     */
    private void processTransactions(Transaction transaction) {
        if (transaction != null && transaction.getSender_id() != transaction.getReceiver_id()) {
            Account sender = accountService.getAccount(transaction.getSender_id());
            Account receiver = accountService.getAccount(transaction.getReceiver_id());

            double senderNewBalance = sender.getBalance() - transaction.getAmount();
            double receiverNewBalance = receiver.getBalance() + transaction.getAmount();
            if(senderNewBalance >= 0) {
                sender.setBalance(senderNewBalance);
                receiver.setBalance(receiverNewBalance);
                accountService.update(sender);
                accountService.update(receiver);

                transaction.setStatus(Transaction.Status.DONE);
                transactionService.update(transaction);

            } else {
                transaction.setStatus(Transaction.Status.FAILED);
                transactionService.update(transaction);
//                throw new InsufficientBalanceException("Sender has insufficient balance: " + sender.getId());
                logger.info("Sender has insufficient balance: " + sender.getId());
                logger.info("Transaction failed: " + transaction.getId());
            }
        }
    }
}
