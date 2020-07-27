package com.circle.services;

import com.circle.models.Account;
import com.circle.models.Transaction;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton Service class that implements DB interaction for the "transactions" table
 */
public class TransactionService {
    private final static Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final DBI dbi;
    private static TransactionService transactionService = null;

    private TransactionService(DBI dbi) {
        this.dbi = dbi;
    }

    public static TransactionService getInstance(DBI dbi) {
        if(transactionService == null) {
            transactionService = new TransactionService(dbi);
        }
        return transactionService;
    }

    /**
     * Function to get transaction information by ID.
     * @param id uuid of the transaction.
     * @return transaction object with the corresponding ID
     */
    public Transaction getTransaction(UUID id) {
        logger.info("Getting transaction info for ID: " + id);
        String query = "SELECT * from transactions WHERE id = ?";
        Handle handle = dbi.open();
        try {
            Map<String, Object> result = handle.createQuery(query).bind(0, id).first();
            ObjectMapper objectMapper = new ObjectMapper();
            Transaction transaction = objectMapper.convertValue(result, Transaction.class);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
            handle.close();
            if (transaction == null) {
                throw new IllegalArgumentException("Unable to find transaction by ID: " + id);
            }
            return transaction;
        } catch (Exception exception) {
            logger.error("Unable to find transaction, pleases check the transaction id", exception.getCause());
            throw exception;
        }
    }

    /**
     * Function to get transactions by status.
     * @param status status of transaction(pending/done/failed).
     * @return list of transaction objects of the input status.
     */
    public List<Transaction> getTransactionsByStatus(Transaction.Status status) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * from transactions WHERE STATUS = ?";
        Handle handle = dbi.open();
        try {
            List<Map<String, Object>> results = handle.createQuery(query).bind(0, status.getValue()).list();
            ObjectMapper objectMapper = new ObjectMapper();
            for(Map<String, Object> row : results) {
                Transaction transaction = objectMapper.convertValue(row, Transaction.class);
                transactions.add(transaction);
            }
        } catch (Exception exception) {
            logger.error("Unable to find transactions, pleases check the status", exception.getCause());
            throw exception;
        }
        return transactions;
    }

    /**
     * Function to add transaction to the transactions table.
     * @param transaction transaction object to be added.
     * @return transaction object that has been added.
     */
    public Transaction addTransaction(Transaction transaction) {
        logger.info("Writing new transaction [{}] to DB", transaction);
        try {
            String query = "INSERT INTO transactions(id, sender_id, receiver_id, amount, status, created_at) VALUES (?,?,?,?,?,?)";
            dbi.useHandle(handle -> handle.execute(query,
                    transaction.getId(),
                    transaction.getSender_id(),
                    transaction.getReceiver_id(),
                    transaction.getAmount(),
                    transaction.getStatus().getValue(),
                    transaction.getCreated_at()));
            return transaction;
        } catch (DBIException exception) {
            logger.error("Unable to create account!", exception.getCause());
            throw exception;
        }
    }

    /**
     * Function to update transaction information in the transactions table.
     * @param transaction Object to be updated by ID. Only status can be updated at the moment.
     */
    public void update(Transaction transaction) {
        logger.info("Updating transaction [{}] to DB", transaction);
        try {
            String query = "UPDATE transactions set status=? WHERE id=?";
            dbi.useHandle(handle -> handle.execute(query,
                    transaction.getStatus().getValue(),
                    transaction.getId()));
        } catch (DBIException exception) {
            logger.error("Unable to create account!", exception.getCause());
            throw exception;
        }
    }
}
