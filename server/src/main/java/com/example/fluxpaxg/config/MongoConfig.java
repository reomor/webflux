package com.example.fluxpaxg.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

@Profile("!test")
@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        String databaseName = environment.getProperty("spring.data.mongodb.database");
        return databaseName == null ? "test" : databaseName;
    }
}
