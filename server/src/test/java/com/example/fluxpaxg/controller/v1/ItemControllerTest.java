package com.example.fluxpaxg.controller.v1;

import com.example.fluxpaxg.docs.Item;
import com.example.fluxpaxg.initialize.ItemDataInitializer;
import com.example.fluxpaxg.repository.ItemReactiveRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static com.example.fluxpaxg.controller.v1.ItemController.ITEM_ALL_V1;
import static com.example.fluxpaxg.initialize.ItemDataInitializer.ITEM;
import static com.example.fluxpaxg.initialize.ItemDataInitializer.ITEM_ID;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemControllerTest {

    static final Logger log = LoggerFactory.getLogger(ItemControllerTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(ItemDataInitializer.data()))
            .flatMap(itemReactiveRepository::save)
            .doOnNext(item -> log.info("Inserted: {}", item))
            .blockLast();
    }

    @Test
    void getAllItems() {
        webTestClient.get()
            .uri(ITEM_ALL_V1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Item.class)
            .hasSize(ItemDataInitializer.data().size());
    }

    @Test
    void getAllItems2() {
        webTestClient.get()
            .uri(ITEM_ALL_V1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Item.class)
            .hasSize(ItemDataInitializer.data().size())
            .consumeWith(listEntityExchangeResult -> {
                Objects.requireNonNull(listEntityExchangeResult.getResponseBody())
                    .forEach(item -> Assertions.assertNotNull(item.getId()));
            });
    }

    @Test
    void getAllItems3() {
        Flux<Item> itemFlux = webTestClient.get()
            .uri(ITEM_ALL_V1)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(Item.class)
            .getResponseBody();
        StepVerifier.create(itemFlux.log())
            .expectSubscription()
            .expectNextCount(ItemDataInitializer.data().size())
            .verifyComplete();
    }

    @Test
    void getById() {
        webTestClient.get()
            .uri(ITEM_ALL_V1.concat("/{id}"), ITEM_ID)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.price", ITEM.getPrice());
    }

    @Test
    void getById_notFound() {
        webTestClient.get()
            .uri(ITEM_ALL_V1.concat("/{id}"), UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void createItem() {
        Item item = new Item(null, "Huawei", 299.99);
        webTestClient.post()
            .uri(ITEM_ALL_V1)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.description").isEqualTo(item.getDescription())
            .jsonPath("$.price").isEqualTo(item.getPrice());
    }

    @Test
    void deleteItemById() {
        webTestClient.delete()
            .uri(ITEM_ALL_V1.concat("/{id}"), ITEM_ID)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Void.class);
    }

    @Test
    void updateItem() {
        double newPrice = 1111.1111;
        Item item = new Item(null, ITEM.getDescription(), newPrice);

        webTestClient.put()
            .uri(ITEM_ALL_V1.concat("/{id}"), ITEM.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.price", newPrice);
    }

    @Test
    void updateItem_NotFound() {
        double newPrice = 1111.1111;
        Item item = new Item(null, ITEM.getDescription(), newPrice);
        webTestClient.put()
            .uri(ITEM_ALL_V1.concat("/{id}"), UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
            .exchange()
            .expectStatus().isNotFound();
    }
}