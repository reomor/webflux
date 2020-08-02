package com.example.fluxpaxg.handlers;

import com.example.fluxpaxg.docs.Item;
import com.example.fluxpaxg.initialize.ItemDataInitializer;
import com.example.fluxpaxg.repository.ItemReactiveRepository;
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

import java.util.UUID;

import static com.example.fluxpaxg.initialize.ItemDataInitializer.ITEM;
import static com.example.fluxpaxg.initialize.ItemDataInitializer.ITEM_ID;
import static com.example.fluxpaxg.router.ItemRouter.ITEM_V1_FUNCTIONAL;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemHandlerTest {

    static final Logger log = LoggerFactory.getLogger(ItemHandlerTest.class);

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
            .uri(ITEM_V1_FUNCTIONAL)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Item.class)
            .hasSize(ItemDataInitializer.data().size());
    }

    @Test
    void getItemById() {
        webTestClient.get()
            .uri(ITEM_V1_FUNCTIONAL.concat("/{id}"), ITEM_ID)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Item.class)
            .isEqualTo(ITEM);
    }

    @Test
    void getItemById_notFound() {
        webTestClient.get()
            .uri(ITEM_V1_FUNCTIONAL.concat("/{id}"), UUID.randomUUID().toString())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void createItem() {
        var item = new Item(null, "New description", 1111.1111);
        webTestClient.post()
            .uri(ITEM_V1_FUNCTIONAL)
            .bodyValue(item)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.description").isEqualTo(item.getDescription())
            .jsonPath("$.price").isEqualTo(item.getPrice());
    }

    @Test
    void deleteItem() {
        webTestClient.delete()
            .uri(ITEM_V1_FUNCTIONAL.concat("/{id}"), ITEM_ID)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody().isEmpty();
    }

    @Test
    void updateItem() {
        var item = new Item(null, ITEM.getDescription(), 1111.1111);
        webTestClient.put()
            .uri(ITEM_V1_FUNCTIONAL.concat("/{id}"), ITEM_ID)
            .bodyValue(item)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.description").isEqualTo(item.getDescription())
            .jsonPath("$.price").isEqualTo(item.getPrice());
    }

    @Test
    void updateItem_notFound() {
        var item = new Item(null, ITEM.getDescription(), 1111.1111);
        webTestClient.put()
            .uri(ITEM_V1_FUNCTIONAL.concat("/{id}"), UUID.randomUUID().toString())
            .bodyValue(item)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void getRuntimeException() {
        webTestClient.get()
            .uri("/v1/func/exception/runtime")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody(String.class)
            .isEqualTo("Runtime Exception functional");
    }
}