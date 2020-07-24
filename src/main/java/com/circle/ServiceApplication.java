package com.circle;

import com.circle.resources.AccountResource;
import com.circle.resources.TimeResource;
import com.circle.resources.TransactionResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class ServiceApplication extends Application<ServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new ServiceApplication().run(args);
    }

    @Override
    public void run(ServiceConfiguration config, Environment env) throws Exception {
        // Get a database handle
        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(env, config.getDatabase(), "interview");

        // Register our sole resource
        env.jersey().register(new TimeResource(dbi));
        env.jersey().register(new AccountResource(dbi));
        env.jersey().register(new TransactionResource(dbi));

        // Registering JobExecutionService to run background jobs
        env.lifecycle().manage(new JobExecutionService(dbi));
    }
}
