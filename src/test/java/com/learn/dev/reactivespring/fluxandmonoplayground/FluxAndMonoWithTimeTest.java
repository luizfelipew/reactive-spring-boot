package com.learn.dev.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .log();// starts from 0 --> .....

        infiniteFlux
                .subscribe((element) -> System.out.println("Value is : " + element));

        Thread.sleep(3000);

    }

    @Test
    void infiniteSequenceTest() {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectNext(0L, 1L, 2L)
                .verifyComplete();

    }

    @Test
    void infiniteSequenceMap() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .map(l -> l.intValue())
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectNext(0, 1, 2)
                .verifyComplete();

    }

    @Test
    void infiniteSequenceMap_withDelay() {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> l.intValue())
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectNext(0, 1, 2)
                .verifyComplete();

    }
}
