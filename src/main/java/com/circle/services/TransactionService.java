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

import java.util.Map;
import java.util.UUID;

public class TransactionService {
    private final static Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final DBI dbi;

    public TransactionService(DBI dbi) {
        this.dbi = dbi;
    }

    public Transaction getTransaction(String id) {
        logger.info("Getting transaction info for ID: " + id);
        String query = "SELECT * from transactions WHERE id = ?";
        Handle handle = dbi.open();
        try {
            Map<String, Object> map = handle.createQuery(query).bind(0, UUID.fromString(id)).first();
            ObjectMapper objectMapper = new ObjectMapper();
            logger.info("Read Map:: " + map);
            Transaction transaction = objectMapper.convertValue(map, Transaction.class);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
            handle.close();
            return transaction;
        } catch (Exception exception) {
            logger.error("Unable to find transaction, pleases check the transaction id", exception.getCause());
            throw exception;
        }
    }

    public Transaction addTransaction(Transaction transaction) {
        logger.info("Writing new transaction [{}] to DB", transaction);
        try {
            String query = "INSERT INTO transactions(id, sender_id, receiver_id, amount, status, created_at) VALUES (?,?,?,?,?,?)";
            dbi.useHandle(handle -> handle.execute(query,
                    transaction.getId(),
//                    transaction.getSenderId(),
                    transaction.getSender_id(),
//                    transaction.getReceiverId(),
                    transaction.getReceiver_id(),
                    transaction.getAmount(),
                    transaction.getStatus().getValue(),
//                    transaction.getCreatedAt()));
                    transaction.getCreated_at()));
            return transaction;
        } catch (DBIException exception) {
            logger.error("Unable to create account!", exception.getCause());
            throw exception;
        }
    }
}
