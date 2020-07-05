package com.example.fluxpaxg.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
            .delayElements(Duration.ofSeconds(1));

        // cold publisher - emit values from beginning
        stringFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));
        Thread.sleep(2_000);

        // cold publisher - emit values from beginning
        stringFlux.subscribe(s -> System.out.println("Subscriber 2: " + s));
        Thread.sleep(4_000);
    }

    @Test
    void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
            .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));
        Thread.sleep(3_000);

        // hot publisher - get values since connect time
        connectableFlux.subscribe(s -> System.out.println("Subscriber 2: " + s));
        Thread.sleep(3_000);
    }
}
