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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/transactions")
public class TransactionResource {
    private final static Logger logger = LoggerFactory.getLogger(TransactionResource.class);
    private final TransactionService transactionService;

    public TransactionResource(DBI dbi) {
        transactionService = TransactionService.getInstance(dbi);
    }

    /**
     * API to get transaction information by ID
     * @param id ID of the transaction
     * @return JSON object containing the transaction information.
     */
    @GET
    @Path("/{id}")
    public Response getTransaction(@PathParam(("id")) String id) {
        try {
            Transaction transaction = transactionService.getTransaction(UUID.fromString(id));
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

    /**
     * API to get transactions by status.
     * @param status Status of transaction: Failed/Pending/Done
     * @return JSON object containing transactions information.
     */
    @GET
    public Response getTransactionByStatus(@QueryParam(("status")) String status) {
        try {
            List<Transaction> transactions = transactionService.getTransactionsByStatus(Transaction.Status.valueOf(status.toUpperCase()));
            return Response
                    .ok()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(transactions)
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
     * API to add transactions to the queue.
     * @param transactionRequest API body containing the sender account, receiver account and the amount needed to be transferred.
     * @return Response with success if transaction successfully added or error message in case of failure.
     */
    @POST
    public Response addTransaction(Map<String, Object> transactionRequest) {
        try {

            logger.info(String.valueOf(transactionRequest));
            Transaction transaction = new Transaction(UUID.fromString((String) transactionRequest.get("senderId")),
                    UUID.fromString((String) transactionRequest.get("receiverId")),
                    (Double) transactionRequest.get("amount"));

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
