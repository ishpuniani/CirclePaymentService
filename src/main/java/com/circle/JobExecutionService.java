package com.circle;

import com.circle.jobs.ProcessTransactionsJob;
import io.dropwizard.lifecycle.Managed;
import org.skife.jdbi.v2.DBI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * https://stackoverflow.com/questions/34653177/running-async-jobs-in-dropwizard-and-polling-their-status
 * A wrapper around the   ScheduledExecutorService so all jobs can start when the server starts, and
 * automatically shutdown when the server stops.
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */

public class JobExecutionService implements Managed {

    private final DBI dbi;
    public JobExecutionService(DBI dbi) {
        this.dbi = dbi;
    }

    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    @Override
    public void start() throws Exception {
        System.out.println("Starting jobs");
        service.scheduleAtFixedRate(new ProcessTransactionsJob(dbi), 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Shutting down");
        service.shutdown();
    }
}