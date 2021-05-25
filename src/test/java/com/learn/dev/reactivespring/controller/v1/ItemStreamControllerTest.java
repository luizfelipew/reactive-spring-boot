package com.learn.dev.reactivespring.controller.v1;

import com.learn.dev.reactivespring.constants.ItemConstants;
import com.learn.dev.reactivespring.document.Item;
import com.learn.dev.reactivespring.document.ItemCapped;
import com.learn.dev.reactivespring.repository.ItemReactiveCappedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamControllerTest {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    ReactiveMongoOperations mongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        mongoOperations.dropCollection(ItemCapped.class)
                .then(mongoOperations.createCollection(ItemCapped.class,
                        CollectionOptions.empty().maxDocuments(20).size(50000).capped()))
                .subscribe();

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(100))
                .map(i -> new ItemCapped(null, "Random Item " + i, (100.00 + i))).take(5);

        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .doOnNext(itemCapped -> {
                    System.out.println("Inserted Item in setUp " + itemCapped);
                })
                .blockLast();
    }

    @Test
    void testStreamAllItems() {

        Flux<ItemCapped> itemCappedFlux = webTestClient.get()
                .uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult((ItemCapped.class))
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();

    }
}
