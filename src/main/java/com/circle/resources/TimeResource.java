package com.circle.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.skife.jdbi.v2.DBI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class TimeResource {

    private final DBI dbi;

    public TimeResource(DBI dbi) {
        this.dbi = dbi;
    }

    /**
     * Endpoint to return the current time.
     */
    @GET
    public Response getTime() {
        // Get the current database time
        Instant now = dbi.withHandle(h ->
            h.createQuery("SELECT now()")
                .mapTo(Instant.class)
                .first());

        TimeResponse response = new TimeResponse(now);

        // Return it to the caller
        return Response
            .ok()
            .type(MediaType.APPLICATION_JSON)
            .entity(response)
            .build();
    }

    public static class TimeResponse {

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private Instant data;

        public TimeResponse(Instant data) {
            this.data = data;
        }

        public Instant getData() {
            return data;
        }
    }
}
