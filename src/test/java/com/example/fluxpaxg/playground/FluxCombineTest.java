package com.example.fluxpaxg.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Arrays;

public class FluxCombineTest {

    @Test
    public void fluxCombineMerge() {
        Flux<String> stringFlux1 = Flux.fromIterable(Arrays.asList("A", "B", "C"));
        Flux<String> stringFlux2 = Flux.fromIterable(Arrays.asList("D", "E"));
        Flux<String> merge = Flux.merge(stringFlux1, stringFlux2);

        StepVerifier.create(merge.log())
            .expectSubscription()
            .expectNext("A", "B", "C", "D", "E")
            .verifyComplete();
    }

    @Test
    public void fluxCombineMergeWithDelay() {
        Flux<String> stringFlux1 = Flux.fromIterable(Arrays.asList("A", "B", "C"))
            .delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.fromIterable(Arrays.asList("D", "E"));
        Flux<String> merge = Flux.merge(stringFlux1, stringFlux2);

        StepVerifier.create(merge.log())
            .expectSubscription()
            .expectNextCount(5) //order is not guaranteed
//            .expectNext("A", "B", "C", "D", "E")
            .verifyComplete();
    }

    @Test
    public void fluxCombineConcat() {
        Flux<String> stringFlux1 = Flux.fromIterable(Arrays.asList("A", "B", "C"))
            .delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.fromIterable(Arrays.asList("D", "E"));
        Flux<String> concat = Flux.concat(stringFlux1, stringFlux2);

        StepVerifier.create(concat.log())
            .expectSubscription()
            .expectNext("A", "B", "C", "D", "E")
            .verifyComplete();
    }

    @Test
    public void fluxCombineZipConcat() {
        Flux<String> stringFlux1 = Flux.fromIterable(Arrays.asList("A", "B", "C"))
            .delayElements(Duration.ofSeconds(1));
        Flux<String> stringFlux2 = Flux.fromIterable(Arrays.asList("D", "E", "F"));
        Flux<String> zip = Flux.zip(stringFlux1, stringFlux2, String::concat);

        StepVerifier.create(zip.log())
            .expectSubscription()
            .expectNext("AD", "BE", "CF")
            .verifyComplete();
    }
}
