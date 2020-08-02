package com.example.fluxpaxg.initialize;

import com.example.fluxpaxg.docs.Item;
import com.example.fluxpaxg.docs.ItemCapped;
import com.example.fluxpaxg.repository.ItemCappedReactiveRepository;
import com.example.fluxpaxg.repository.ItemReactiveRepository;
import com.mongodb.internal.async.client.AsyncMongoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    public static final String ITEM_ID = UUID.randomUUID().toString();
    public static final Item ITEM = new Item(ITEM_ID, "IPhoone 20", 9999.0);

    private final ItemReactiveRepository itemReactiveRepository;
    private final ItemCappedReactiveRepository itemCappedReactiveRepository;
    private final MongoOperations mongoOperations;

    public static List<Item> data() {
        return Arrays.asList(
            new Item(null, "TV", 1000.0),
            new Item(null, "Phone", 250.0),
            new Item(null, "Player", 150.0),
            ITEM
        );
    }

    @Override
    public void run(String... args) throws Exception {
        initDataSetUp();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    private void dataSetUpForCappedCollection() {
        var itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
            .map(number -> new ItemCapped(null, "Random Item" + number, (100.0 + number)));

        itemCappedReactiveRepository.insert(itemCappedFlux)
            .subscribe(itemCapped -> log.info("Inserted ItemCapped: " + itemCapped));
    }

    private void initDataSetUp() {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(data()))
            .flatMap(itemReactiveRepository::save)
            .thenMany(itemReactiveRepository.findAll())
            .subscribe(item -> log.info("Inserted: {}", item));
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(
            ItemCapped.class,
            CollectionOptions.empty()
                .maxDocuments(20)
                .size(50000)
                .capped()
        );
    }
}
