package com.example.fluxpaxg.initialize;

import com.example.fluxpaxg.docs.Item;
import com.example.fluxpaxg.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initDataSetUp();
    }

    public static final String ITEM_ID = UUID.randomUUID().toString();
    public static final Item ITEM = new Item(ITEM_ID, "IPhoone 20", 9999.0);

    public static List<Item> data() {
        return Arrays.asList(
            new Item(null, "TV", 1000.0),
            new Item(null, "Phone", 250.0),
            new Item(null, "Player", 150.0),
            ITEM
        );
    }

    private void initDataSetUp() {
        itemReactiveRepository.deleteAll()
            .thenMany(Flux.fromIterable(data()))
            .flatMap(itemReactiveRepository::save)
            .thenMany(itemReactiveRepository.findAll())
            .subscribe(item -> log.info("Inserted: {}", item));
    }
}
