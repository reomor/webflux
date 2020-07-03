package com.example.fluxpaxg.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxFactoryMethods {

    private List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxIterable() {
        Flux<String> stringFlux = Flux.fromIterable(names).log();
        StepVerifier.create(stringFlux)
            .expectNext("adam", "anna", "jack", "jenny")
            .verifyComplete();
    }

    @Test
    public void fluxArray() {
        String[] strings = {"adam", "anna", "jack", "jenny"};
        Flux<String> stringFlux = Flux.fromArray(strings).log();
        StepVerifier.create(stringFlux)
            .expectNext("adam", "anna", "jack", "jenny")
            .verifyComplete();
    }

    @Test
    public void fluxStream() {
        Flux<String> stringFlux = Flux.fromStream(names.stream());
        StepVerifier.create(stringFlux)
            .expectNext("adam", "anna", "jack", "jenny")
            .verifyComplete();
    }

    @Test
    public void monoEmpty() {
        Mono<String> objectMono = Mono.justOrEmpty(null);
        StepVerifier.create(objectMono.log())
            .expectNext()
            .verifyComplete();
    }

    @Test
    public void monoSupplier() {
        Supplier<String> stringSupplier = () -> "adam";
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);
        StepVerifier.create(stringMono.log())
            .expectNext("adam")
            .verifyComplete();
    }

    @Test
    public void fluxRange() {
        Flux<Integer> range = Flux.range(1, 5);
        StepVerifier.create(range.log())
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }
}
