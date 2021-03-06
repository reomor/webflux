package com.example.fluxpaxg.controller.v1;

import com.example.fluxpaxg.docs.ItemCapped;
import com.example.fluxpaxg.repository.ItemCappedReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemStreamControllerTest {

    @Autowired
    private ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(
            ItemCapped.class,
            CollectionOptions.empty()
                .maxDocuments(20)
                .size(50000)
                .capped()
        );

        var itemCappedFlux = Flux.interval(Duration.ofMillis(1))
            .map(number -> new ItemCapped(null, "Random Item" + number, (100.0 + number)))
            .take(5);

        itemCappedReactiveRepository.insert(itemCappedFlux)
            .doOnNext(itemCapped -> System.out.println("Inserted ItemCapped: " + itemCapped))
            .blockLast();
    }

    @Test
    void testItemStream() {

        Flux<ItemCapped> itemCappedFlux = webTestClient.get()
            .uri(ItemStreamController.ITEM_STREAM_V1)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(ItemCapped.class)
            .getResponseBody()
            .take(5);

        StepVerifier.create(itemCappedFlux)
            .expectSubscription()
            .expectNextCount(5)
            .thenCancel()
            .verify();
    }
}