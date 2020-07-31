package com.example.fluxpaxg.router;

import com.example.fluxpaxg.handlers.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ItemRouter {

    public static final String ITEM_ALL_V1_FUNCTIONAL = "/v1/func/items";
    public static final String ID = "/{id}";

    @Bean
    public RouterFunction<ServerResponse> itemRoute(ItemHandler itemHandler) {
        return RouterFunctions.route(
            GET(ITEM_ALL_V1_FUNCTIONAL)
                .and(accept(MediaType.APPLICATION_JSON)),
            itemHandler::getAllItems
        ).andRoute(
            GET(ITEM_ALL_V1_FUNCTIONAL.concat(ID))
                .and(accept(MediaType.APPLICATION_JSON)),
            itemHandler::getItemById
        ).andRoute(
            POST(ITEM_ALL_V1_FUNCTIONAL)
                .and(accept(MediaType.APPLICATION_JSON)),
            itemHandler::createItem
        ).andRoute(DELETE(ITEM_ALL_V1_FUNCTIONAL.concat(ID))
            .and(accept(MediaType.APPLICATION_JSON)),
            itemHandler::deleteItem
        ).andRoute(PUT(ITEM_ALL_V1_FUNCTIONAL.concat(ID))
            .and(accept(MediaType.APPLICATION_JSON)),
            itemHandler::updateItem
        );
    }
}
