package com.example.fluxpaxg.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
@DirtiesContext
class FluxControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void fluxApproach() {
        Flux<Integer> integerFlux = webTestClient.get()
            .uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(Integer.class)
            .getResponseBody();

        StepVerifier.create(integerFlux)
            .expectSubscription()
            .expectNext(1, 2, 3, 4)
            .verifyComplete();
    }

    @Test
    void fluxApproach2() {
        webTestClient.get()
            .uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Integer.class)
            .hasSize(4);
    }

    @Test
    void fluxApproach3() {
        List<Integer> expected = Arrays.asList(1, 2, 3, 4);
        EntityExchangeResult<List<Integer>> result = webTestClient.get()
            .uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Integer.class)
            .returnResult();

        assertEquals(expected, result.getResponseBody());
    }

    @Test
    void fluxApproach4() {
        List<Integer> expected = Arrays.asList(1, 2, 3, 4);
        webTestClient.get()
            .uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(Integer.class)
            .consumeWith(listEntityExchangeResult -> assertEquals(expected, listEntityExchangeResult.getResponseBody()));
    }

    @Test
    void fluxInfiniteStream() {
        Flux<Long> longFlux = webTestClient.get()
            .uri("/fluxstream")
            .accept(MediaType.APPLICATION_STREAM_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(Long.class)
            .getResponseBody();

        StepVerifier.create(longFlux.log())
            .expectNext(0L)
            .expectNext(1L)
            .expectNext(2L)
            .thenCancel()
            .verify();
    }

    @Test
    void mono() {
        webTestClient.get()
            .uri("/mono")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Integer.class)
            .consumeWith(listEntityExchangeResult ->
                assertEquals(1, listEntityExchangeResult.getResponseBody())
            );
    }
}