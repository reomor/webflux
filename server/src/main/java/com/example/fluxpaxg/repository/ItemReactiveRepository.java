package com.example.fluxpaxg.repository;

import com.example.fluxpaxg.docs.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
    /**
     * Get item by description
     *
     * @param description item description
     * @return item
     */
    Mono<Item> getByDescription(String description);
}
