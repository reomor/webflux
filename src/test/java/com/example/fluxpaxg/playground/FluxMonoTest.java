package com.example.fluxpaxg.playground;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

//@WebFluxTest
//@ExtendWith(SpringExtension.class)
public class FluxMonoTest {

    static final Logger log = LoggerFactory.getLogger(FluxMonoTest.class);

    @Test
    public void fluxTest() {
        Flux<String> flux = Flux.just("Spring", "Summer", "Autumn")
            //.concatWith(Flux.error(new RuntimeException("Flux exception")))
            .concatWith(Flux.just("After exception"))
            .log();
        flux.subscribe(
            log::info,
            e -> log.error("!!! {}", e.getMessage()),
            () -> log.info("END of everything")
        );
    }

    @Test
    public void fluxTest_WithoutError() {
        Flux<String> flux = Flux.just("Spring", "Summer", "Autumn")
            .log();

        StepVerifier.create(flux)
            .expectNext("Spring")
            .expectNext("Summer")
            .expectNext("Autumn")
            .verifyComplete();
    }

    @Test
    public void fluxTest_WithError() {
        String fluxException = "Flux exception";
        Flux<String> flux = Flux.just("Spring", "Summer", "Autumn")
            .concatWith(Flux.error(new RuntimeException(fluxException)))
            .log();

        StepVerifier.create(flux)
            .expectNext("Spring")
            .expectNext("Summer")
            .expectNext("Autumn")
            //.expectError(RuntimeException.class)
            .expectErrorMessage(fluxException)
            .verify();
    }

    @Test
    public void fluxTestCount_WithError() {
        String fluxException = "Flux exception";
        Flux<String> flux = Flux.just("Spring", "Summer", "Autumn")
            .concatWith(Flux.error(new RuntimeException(fluxException)))
            .log();

        StepVerifier.create(flux)
            .expectNextCount(3)
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    public void monoTest() {
        Mono<String> mono = Mono.just("Spring").log();
        StepVerifier.create(mono)
            .expectNext("Spring")
            .verifyComplete();
    }

    @Test
    public void monoTest_WithError() {
        StepVerifier.create(Mono.error(new RuntimeException("Mono exception")).log())
            .expectError(RuntimeException.class)
            .verify();
    }
}
