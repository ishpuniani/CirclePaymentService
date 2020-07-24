package com.circle.resources;

import com.circle.models.Account;
import com.circle.services.AccountService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/accounts")
public class AccountResource {
    private final static Logger logger = LoggerFactory.getLogger(AccountResource.class);

    private final AccountService accountService;

    public AccountResource(DBI dbi) {
        this.accountService = AccountService.getInstance(dbi);
    }

    /**
     * API to get account information
     * @param id: id of the account to be queried
     * @return the account object corresponding to the ID or ErrorMessage.
     */
    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") String id) {
        try {
            Account account = accountService.getAccount(UUID.fromString(id));
            return Response
                    .ok()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(account)
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity("\"ErrorMessage\":\"" + e.getMessage() + "\"")
                    .build();
        }
    }

    @GET
    public Response getAccountByEmail(@QueryParam("email") String email) {
        try {
            Account account = accountService.getAccountByEmail(email);
            return Response
                    .ok()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(account)
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity("\"ErrorMessage\":\"" + e.getMessage() + "\"")
                    .build();
        }
    }

    /**
     * API to create new account in DB
     * @param account: Account info: name, email, balance
     * @return an object of the created account
     */
    @POST
    public Response addAccount(Account account) {
        try {
            Account savedAccount = accountService.addAccount(account);
            return Response
                    .ok()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(savedAccount)
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity("\"ErrorMessage\":\"" + e.getMessage() + "\"")
                    .build();
        }
    }

}
