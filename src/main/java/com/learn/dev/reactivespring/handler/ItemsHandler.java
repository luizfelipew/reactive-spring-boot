package com.learn.dev.reactivespring.handler;

import com.learn.dev.reactivespring.document.Item;
import com.learn.dev.reactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemsHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);

        return itemMono
                .flatMap(item ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromObject(item)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> itemToBeInserted = serverRequest.bodyToMono(Item.class);

        return itemToBeInserted
                .flatMap(item ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Void> deleteItem = itemReactiveRepository.deleteById(id);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }
}
