package com.circle.services;

import com.circle.models.Account;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton Service class that implements DB interaction for the "accounts" table
 */
public class AccountService {
    private final static Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final DBI dbi;

    private AccountService(DBI dbi) {
        this.dbi = dbi;
    }

    private static AccountService accountService = null;

    public static AccountService getInstance(DBI dbi) {
        if(accountService == null) {
            accountService = new AccountService(dbi);
        }
        return accountService;
    }

    /**
     * Function to get account information by ID.
     * @param id uuid ID of the account.
     * @return account object with the corresponding ID.
     */
    public Account getAccount(UUID id) {
        logger.info("Getting account info for ID: " + id);
        String query = "SELECT * from accounts WHERE id = ?";
        Handle handle = dbi.open();
        try {
            Map<String, Object> result = handle.createQuery(query).bind(0, id).first();
            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            Account account = objectMapper.convertValue(result, Account.class);
            handle.close();
            if (account == null) {
                throw new IllegalArgumentException("Unable to find account by ID: " + id);
            }
            logger.info("Getting Account:: " + account);
            return account;
        } catch (Exception exception) {
            logger.error("Unable to find account, pleases check the account id", exception.getCause());
            throw exception;
        }
    }

    /**
     * Function to get account by email.
     * @param email email id of the account.
     * @return account object with the corresponding email id.
     */
    public Account getAccountByEmail(String email) {
        logger.info("Getting account info for email: " + email);
        String query = "SELECT * from accounts WHERE email = ?";
        Handle handle = dbi.open();
        try {
            Map<String, Object> result = handle.createQuery(query).bind(0, email).first();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            Account account = objectMapper.convertValue(result, Account.class);
            handle.close();
            if (account == null) {
                throw new IllegalArgumentException("Unable to find account by email: " + email);
            }
            return account;
        } catch (Exception exception) {
            logger.error("Unable to find account, pleases check the account id", exception.getCause());
            throw exception;
        }
    }

    /**
     * Function to add account with the given details.
     * @param account account object to be added to the table "accounts".
     * @return account object added to the table.
     */
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

    /**
     * Function to update account details in the table "accounts".
     * @param account account object to be updated.
     */
    public void update(Account account) {
        logger.info("Updating account [{}] to DB", account);
        try {
            String query = "UPDATE accounts set name=?, email=?, balance=? WHERE id=?";
            dbi.useHandle(handle -> handle.execute(query,
                    account.getName(),
                    account.getEmail(),
                    account.getBalance(),
                    account.getId()));
        } catch (DBIException exception) {
            logger.error("Unable to create account!", exception.getCause());
            throw exception;
        }
    }
}
