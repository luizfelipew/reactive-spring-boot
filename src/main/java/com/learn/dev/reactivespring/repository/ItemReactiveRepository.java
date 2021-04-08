package com.learn.dev.reactivespring.repository;

import com.learn.dev.reactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
}
