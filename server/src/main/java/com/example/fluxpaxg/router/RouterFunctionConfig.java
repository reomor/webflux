package com.example.fluxpaxg.router;

import com.example.fluxpaxg.handlers.SampleHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterFunctionConfig {
    @Bean
    public RouterFunction<ServerResponse> route(SampleHandler handler) {
        return RouterFunctions.route(
            GET("/functional/flux")
                .and(accept(MediaType.APPLICATION_JSON)),
            handler::flux
        ).andRoute(GET("/functional/mono")
                .and(accept(MediaType.APPLICATION_JSON)),
            handler::mono
        );
    }
}
