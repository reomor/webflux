package com.example.fluxpaxg.controller.v1;

import com.example.fluxpaxg.docs.Item;
import com.example.fluxpaxg.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class ItemController {

    public static final String ITEM_ALL_V1 = "/v1/items";

    private final ItemReactiveRepository itemReactiveRepository;

    @Autowired
    public ItemController(ItemReactiveRepository itemReactiveRepository) {
        this.itemReactiveRepository = itemReactiveRepository;
    }

    @GetMapping(ITEM_ALL_V1)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_ALL_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable String id) {
        return itemReactiveRepository.findById(id)
            .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_ALL_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_ALL_V1 + "/{id}")
    public Mono<Void> deleteById(@PathVariable String id) {
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(ITEM_ALL_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(
        @PathVariable String id,
        @RequestBody Item item
    ) {
        return itemReactiveRepository.findById(id)
            .flatMap(currentItem -> {
                currentItem.setDescription(item.getDescription());
                currentItem.setPrice(item.getPrice());
                return itemReactiveRepository.save(currentItem);
            }).map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
