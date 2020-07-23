package com.circle;

import com.circle.jobs.EveryTestJob;
import com.circle.resources.TimeResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jobs.Job;
import io.dropwizard.jobs.JobsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class ServiceApplication extends Application<ServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new ServiceApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
        Job everyJob = new EveryTestJob();
        bootstrap.addBundle(new JobsBundle(everyJob));
    }

    @Override
    public void run(ServiceConfiguration config, Environment env) throws Exception {
        // Get a database handle
        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(env, config.getDatabase(), "interview");

        // Register our sole resource
        env.jersey().register(new TimeResource(dbi));
    }
}
