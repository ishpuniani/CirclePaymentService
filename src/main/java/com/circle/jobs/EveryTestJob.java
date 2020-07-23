package com.circle.jobs;

import io.dropwizard.jobs.Job;
import io.dropwizard.jobs.annotations.Every;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Every("3s")
public class EveryTestJob extends Job {
    @Override
    public void doJob(JobExecutionContext context) throws JobExecutionException {
        // logic run every time and time again
        System.out.println("Hello");
    }
}