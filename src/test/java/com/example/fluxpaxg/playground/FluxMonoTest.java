package com.example.fluxpaxg.playground;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

@WebFluxTest
@ExtendWith(SpringExtension.class)
public class FluxMonoTest {

    static final Logger log = LoggerFactory.getLogger(FluxMonoTest.class);

    @Test
    public void fluxTest() {
        Flux<String> flux = Flux.just("Spring", "Summer", "Autumn")
            .concatWith(Flux.error(new RuntimeException("Flux exception")))
            .concatWith(Flux.just("After exception"))
            .log();
        flux.subscribe(
            log::info,
            e -> log.error("!!! {}", e.getMessage()),
            () -> log.info("END of everything")
        );
    }
}
