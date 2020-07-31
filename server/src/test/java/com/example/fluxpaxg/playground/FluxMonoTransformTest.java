package com.example.fluxpaxg.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxMonoTransformTest {

    private List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxMap() {
        Flux<String> stringFlux = Flux.fromIterable(names)
            .map(String::toUpperCase)
            .log();
        StepVerifier.create(stringFlux)
            .expectNext("ADAM", "ANNA", "JACK", "JENNY")
            .verifyComplete();
    }

    @Test
    public void fluxMapLength() {
        Flux<Integer> integerFlux = Flux.fromIterable(names)
            .map(String::length)
            .repeat(1)
            .log();
        StepVerifier.create(integerFlux)
            .expectNext(4, 4, 4, 5)
            .expectNext(4, 4, 4, 5)
            .verifyComplete();
    }

    @Test
    public void fluxMapFilter() {
        Flux<String> stringFlux = Flux.fromIterable(names)
            .filter(s -> s.length() > 4)
            .map(String::toUpperCase)
            .repeat(1)
            .log();
        StepVerifier.create(stringFlux)
            .expectNext("JENNY")
            .expectNext("JENNY")
            .verifyComplete();
    }

    @Test
    public void fluxFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
            .flatMap(s -> Flux.fromIterable(convertToList(s)))
            .log();
        StepVerifier.create(stringFlux)
            .expectNextCount(10)
            .verifyComplete();
    }

    private static List<String> convertToList(String string) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(string, string);
    }

    @Test
    public void fluxFlatMapParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
            .window(2) // (A,B), (C,D), ...
            .flatMap(flux ->
                flux.map(FluxMonoTransformTest::convertToList)
                    .subscribeOn(parallel())
            )
            .flatMap(Flux::fromIterable)
            .log();
        StepVerifier.create(stringFlux)
            .expectNextCount(10)
            .verifyComplete();
    }

    @Test
    public void fluxFlatMapParallelWithOrder() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E"))
            .window(2) // (A,B), (C,D), ...
//            slow
//            .concatMap(flux ->
//                flux.map(FluxMonoTransformTest::convertToList)
//                    .subscribeOn(parallel())
//            )
            .flatMapSequential(flux ->
                flux.map(FluxMonoTransformTest::convertToList)
                    .subscribeOn(parallel())
            )
            .flatMap(Flux::fromIterable)
            .log();
        StepVerifier.create(stringFlux)
            .expectNextCount(10)
            .verifyComplete();
    }
}
