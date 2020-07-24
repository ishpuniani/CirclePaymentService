package com.circle.resources;

import com.circle.models.Transaction;
import com.circle.services.TransactionService;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/transactions")
public class TransactionResource {
    private final static Logger logger = LoggerFactory.getLogger(TransactionResource.class);
    private final TransactionService transactionService;

    public TransactionResource(DBI dbi) {
        transactionService = new TransactionService(dbi);
    }

    @GET
    @Path("/{id}")
    public Response getTransaction(@PathParam(("id")) String id) {
        try {
            Transaction transaction = transactionService.getTransaction(id);
            return Response
                    .ok()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(transaction)
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity("\"ErrorMessage\":\"" + e.getMessage() + "\"")
                    .build();
        }
    }

    @POST
//    public Response addTransaction(TransactionRequest transactionRequest) {
    public Response addTransaction(Map<String, Object> transactionRequest) {
        try {

            logger.info(String.valueOf(transactionRequest));
            Transaction transaction = new Transaction(UUID.fromString((String) transactionRequest.get("senderId")),
                    UUID.fromString((String) transactionRequest.get("receiverId")),
                    (Double) transactionRequest.get("amount"));
            /*Transaction transaction = new Transaction(transactionRequest.getSenderId(),
                    transactionRequest.getReceiverId(),
                    transactionRequest.getAmount());*/

            logger.info(transaction.toString());
            Transaction savedTransaction = transactionService.addTransaction(transaction);
            return Response
                    .ok()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(savedTransaction)
                    .build();
        } catch (Exception e) {
            logger.error("Exception", e);
            return Response
                    .serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity("\"ErrorMessage\":\"" + e.getMessage() + "\"")
                    .build();
        }
    }
}
