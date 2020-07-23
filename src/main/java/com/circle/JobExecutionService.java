package com.circle;

import com.circle.jobs.HelloWorldJob;
import io.dropwizard.lifecycle.Managed;

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


    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    @Override
    public void start() throws Exception {
        System.out.println("Starting jobs");
        service.scheduleAtFixedRate(new HelloWorldJob(), 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Shutting down");
        service.shutdown();
    }
}