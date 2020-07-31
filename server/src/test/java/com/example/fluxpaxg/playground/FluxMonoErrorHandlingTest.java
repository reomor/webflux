package com.example.fluxpaxg.playground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxMonoErrorHandlingTest {
    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Ex")))
            .concatWith(Flux.just("D"))
            .onErrorResume(e -> {
                System.out.println(e.getMessage());
                return Flux.just("default0", "default1");
            });
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
//            .expectError(RuntimeException.class)
//            .verify();
            .expectNext("default0", "default1")
            .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingResume() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Ex")))
            .concatWith(Flux.just("D"))
            .onErrorResume(e -> {
                System.out.println(e.getMessage());
                return Flux.just("default");
            });
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("default")
            .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Ex")))
            .concatWith(Flux.just("D"))
            .onErrorMap(CustomException::new);
        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectError(CustomException.class)
            .verify();
    }

    @Test
    public void fluxErrorHandlingMapRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Ex")))
            .concatWith(Flux.just("D"))
            .onErrorMap(CustomException::new)
            .retry(1);

        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(CustomException.class)
            .verify();
    }

    @Test
    public void fluxErrorHandlingMapRetryBackOff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(new RuntimeException("Ex")))
            .concatWith(Flux.just("D"))
            .onErrorMap(CustomException::new)
            //.retryBackoff(2, Duration.ofSeconds(2))
        .retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(2)));

        StepVerifier.create(stringFlux.log())
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(IllegalStateException.class)
            .verify();
    }

    private static class CustomException extends Throwable {
        public CustomException(Throwable e) {
            super(e);
        }
    }
}
