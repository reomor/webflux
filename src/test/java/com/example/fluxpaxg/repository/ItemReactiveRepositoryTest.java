package com.example.fluxpaxg.repository;

import com.example.fluxpaxg.docs.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@DataMongoTest
@DirtiesContext
class ItemReactiveRepositoryTest {

    private static final String ID = new UUID(0L, 1L).toString();
    private static final Item ITEM = new Item(ID, "Description 5", 5.0);
    private static final List<Item> itemList = Arrays.asList(
        new Item(null, "Description 1", 1.0),
        new Item(null, "Description 2", 2.0),
        new Item(null, "Description 3", 3.0),
        new Item(null, "Description 4", 4.0),
        ITEM
    );

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(itemList))
            .flatMap(itemReactiveRepository::save)
            .doOnNext(item -> System.out.println("Item is: " + item))
            .blockLast();
    }

    @Test
    void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
            .expectSubscription()
            .expectNextCount(itemList.size())
            .verifyComplete();
    }

    @Test
    void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById(ID))
            .expectSubscription()
            .expectNextMatches(item -> ID.equals(item.getId()) &&
                ITEM.getDescription().equals(item.getDescription()))
            .verifyComplete();
    }

    @Test
    void getByDescription() {
        StepVerifier.create(itemReactiveRepository.getByDescription(ITEM.getDescription()))
            .expectSubscription()
            .expectNext(ITEM)
            .verifyComplete();
    }

    @Test
    void saveItem() {
        Item newItem = new Item(null, "NEW", 0.0);
        StepVerifier.create(itemReactiveRepository.save(newItem))
            .expectSubscription()
            .expectNextMatches(item -> newItem.getId() != null && newItem.getDescription().equals(item.getDescription()))
            .verifyComplete();
    }

    @Test
    void updateItem() {
        double newPrice = 100;
        Mono<Item> itemMono = itemReactiveRepository.findById(ID)
            .map(item -> {
                item.setPrice(newPrice);
                return item;
            })
            .flatMap(itemReactiveRepository::save);
        StepVerifier.create(itemMono)
            .expectSubscription()
            .expectNextMatches(item -> newPrice == item.getPrice())
            .verifyComplete();
    }

    @Test
    void deleteItem() {

        Mono<Void> mono = itemReactiveRepository.findById(ID)
            .map(Item::getId)
            .flatMap(id -> itemReactiveRepository.deleteById(id));

        StepVerifier.create(mono.log())
            .expectSubscription()
            .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findById(ID))
            .expectSubscription()
            .verifyComplete();
    }

    @Test
    void deleteItemByDescription() {

        Mono<Void> mono = itemReactiveRepository.getByDescription(ITEM.getDescription())
            .flatMap(itemReactiveRepository::delete);

        StepVerifier.create(mono.log())
            .expectSubscription()
            .verifyComplete();

        StepVerifier.create(itemReactiveRepository.getByDescription(ITEM.getDescription()))
            .expectSubscription()
            .verifyComplete();
    }

}