package com.example.fluxpaxg.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
class FluxControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void fluxApproach() {
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
    public void fluxApproach2() {
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
    public void fluxApproach3() {
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
    public void fluxApproach4() {
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
}