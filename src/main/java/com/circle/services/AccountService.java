package com.circle.services;

import com.circle.models.Account;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class AccountService {
    private final static Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final DBI dbi;

    public AccountService(DBI dbi) {
        this.dbi = dbi;
    }

    public Account getAccount(String id) {
        logger.info("Getting account info for ID: " + id);
        String query = "SELECT * from accounts WHERE id = ?";
        Handle handle = dbi.open();
        try {
            Map<String, Object> map = handle.createQuery(query).bind(0, UUID.fromString(id)).first();
            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            Account account = objectMapper.convertValue(map, Account.class);
            handle.close();
            return account;
        } catch (Exception exception) {
            logger.error("Unable to find account, pleases check the account id", exception.getCause());
            throw exception;
        }
    }

    public Account getAccountByEmail(String email) {
        logger.info("Getting account info for email: " + email);
        String query = "SELECT * from accounts WHERE email = ?";
        Handle handle = dbi.open();
        try {
            Map<String, Object> map = handle.createQuery(query).bind(0, email).first();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            Account account = objectMapper.convertValue(map, Account.class);
            handle.close();
            return account;
        } catch (Exception exception) {
            logger.error("Unable to find account, pleases check the account id", exception.getCause());
            throw exception;
        }
    }

    public Account addAccount(Account account) {
        logger.info("Writing new account [{}] to DB", account);
        try {
            String query = "INSERT INTO accounts(id, name, email, balance, created_at) VALUES (?,?,?,?,?)";
            dbi.useHandle(handle -> handle.execute(query,
                    account.getId(),
                    account.getName(),
                    account.getEmail(),
                    account.getBalance(),
                    account.getCreatedAt()));
            return account;
        } catch (DBIException exception) {
            logger.error("Unable to create account!", exception.getCause());
            throw exception;
        }
    }

}
