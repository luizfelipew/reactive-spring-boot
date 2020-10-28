package com.learn.dev.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring");

        stringFlux.subscribe(System.out::println);
    }

    @Test
    public void fluxErrorTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Ocurred")))
                .concatWith(Flux.just("After Error"))
                .log();

        stringFlux.subscribe(System.out::println,
                e -> System.err.println("Exception is " + e),
                () -> System.out.println("Completed"));
    }
}
