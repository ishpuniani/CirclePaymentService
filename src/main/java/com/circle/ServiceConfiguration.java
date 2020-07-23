package com.circle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jobs.JobConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class ServiceConfiguration extends Configuration implements JobConfiguration {

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    @JsonProperty("quartz")
    public Map<String,String> quartz;

    @Override
    public Map<String,String> getQuartzConfiguration() {
        return quartz;
    }
}
