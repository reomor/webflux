package fluxpaxg.controller;

import fluxpaxg.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class ItemClientController {

    private static final String ITEMS_ENDPOINT = "/v1/items";

    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping(value = {
        "/client/retrieve",
        "/client/items"
    })
    public Flux<Item> getAllItemsRetrieve() {
        return webClient.get()
            .uri(ITEMS_ENDPOINT)
            .retrieve()
            .bodyToFlux(Item.class)
            .log("Retrieve: ");
    }

    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsExchange() {
        return webClient.get()
            .uri(ITEMS_ENDPOINT)
            .exchange()
            .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
            .log("Exchange: ");
    }

    @GetMapping("/client/items/{id}")
    public Mono<Item> getItemById(@PathVariable String id) {
        return webClient.get()
            .uri(ITEMS_ENDPOINT + "/{id}", id)
            .retrieve()
            .bodyToMono(Item.class)
            .log("Get by id: ");
    }

    @PostMapping("/client/items")
    public Mono<Item> createItem(@RequestBody Item item) {
        return webClient.post()
            .uri(ITEMS_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
            .retrieve()
            .bodyToMono(Item.class)
            .log("Post: ");
    }

    @PutMapping("/client/items/{id}")
    public Mono<Item> updateItem(
        @PathVariable String id,
        @RequestBody Item item
    ) {
        return webClient.put()
            .uri(ITEMS_ENDPOINT + "/{id}", id)
            .body(Mono.just(item), Item.class)
            .retrieve()
            .bodyToMono(Item.class)
            .log("Put: ");
    }

    @DeleteMapping("/client/items/{id}")
    public Mono<Void> deleteItemById(@PathVariable String id) {
        return webClient.delete()
            .uri(ITEMS_ENDPOINT + "/{id}", id)
            .retrieve()
            .bodyToMono(Void.class)
            .log("Delete:");
    }

    @GetMapping("/client/items/error")
    public Flux<Item> exceptionRetrieve() {
        return webClient.get()
            .uri("/v1/items/runtimeException")
            .retrieve()
            .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                Mono<String> stringMono = clientResponse.bodyToMono(String.class);
                return stringMono.flatMap(errorMessage -> {
                    log.error("Got error: {}", errorMessage);
                    throw new RuntimeException(errorMessage);
                });
            }).bodyToFlux(Item.class);
    }

    public Flux<Item> exceptionExchange() {
        return webClient.get()
            .uri("/v1/items/runtimeException")
            .exchange()
            .flatMapMany(clientResponse -> {
                if (clientResponse.statusCode().is5xxServerError()) {
                    return clientResponse.bodyToMono(String.class)
                        .flatMap(errorMessage -> {
                            log.error("Got error: {}", errorMessage);
                            throw new RuntimeException(errorMessage);
                        });
                }
                return clientResponse.bodyToMono(Item.class);
            });
    }
}
