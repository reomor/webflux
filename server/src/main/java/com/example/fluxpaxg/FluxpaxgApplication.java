package com.example.fluxpaxg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class FluxpaxgApplication {
    public static void main(String[] args) {
        SpringApplication.run(FluxpaxgApplication.class, args);
    }
}
