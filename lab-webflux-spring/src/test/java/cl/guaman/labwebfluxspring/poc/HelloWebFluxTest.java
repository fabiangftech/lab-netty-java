package cl.guaman.labwebfluxspring.poc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

class HelloWebFluxTest {

    private static final Logger logger = LoggerFactory.getLogger(HelloWebFluxTest.class);

    @Test
    void testHelloWebFlux() {
        Flux<String> fluxWithDelay = Flux.just("Fabián", "Natalia", "Chechi", "Virgi")
                .log()
                .flatMap(name -> Mono.just("Hello " + name).delayElement(Duration.ofMillis(500)));
        Assertions.assertDoesNotThrow(() -> {
            fluxWithDelay.subscribe(logger::info);
        });
    }

    @Test
    void testBlocking() throws InterruptedException {
        Flux.just("Chile", "Argentina", "Brasil", "Francia")
                .log()
                .map(name -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                    return "The country is " + name;
                }).subscribe(logger::info);
    }
}
