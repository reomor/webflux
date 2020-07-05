package com.example.fluxpaxg.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxMonoTimeTest {

    @Test
    public void fluxInfinite() throws InterruptedException {
        Flux<Long> longFlux = Flux.interval(Duration.ofMillis(200))
            .log(); // from 0 to ...
        longFlux.subscribe(System.out::println);
        Thread.sleep(1_000);
    }

    @Test
    public void fluxInfiniteTest() {
        Flux<Long> longFlux = Flux.interval(Duration.ofMillis(100))
            .take(3)
            .log(); // from 0 to ...
        longFlux.subscribe(System.out::println);
        StepVerifier.create(longFlux)
            .expectSubscription()
            .expectNext(0L, 1L, 2L)
            .verifyComplete();
    }

    @Test
    public void fluxInfiniteTestMap() {
        Flux<Integer> longFlux = Flux.interval(Duration.ofMillis(100))
            .map(aLong -> Integer.valueOf(aLong.intValue()))
            .take(3)
            .log(); // from 0 to ...
        longFlux.subscribe(System.out::println);
        StepVerifier.create(longFlux)
            .expectSubscription()
            .expectNext(0, 1, 2)
            .verifyComplete();
    }

    @Test
    public void fluxInfiniteTestMapDelay() {
        Flux<Integer> longFlux = Flux.interval(Duration.ofMillis(100))
            .delayElements(Duration.ofMillis(1_000))
            .map(aLong -> Integer.valueOf(aLong.intValue()))
            .take(3)
            .log(); // from 0 to ...
        longFlux.subscribe(System.out::println);
        StepVerifier.create(longFlux)
            .expectSubscription()
            .expectNext(0, 1, 2)
            .verifyComplete();
    }
}
