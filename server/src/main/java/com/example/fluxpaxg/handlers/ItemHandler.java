package com.example.fluxpaxg.handlers;

import com.example.fluxpaxg.docs.Item;
import com.example.fluxpaxg.docs.ItemCapped;
import com.example.fluxpaxg.repository.ItemCappedReactiveRepository;
import com.example.fluxpaxg.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ItemHandler {

    private final ItemReactiveRepository itemReactiveRepository;
    private final ItemCappedReactiveRepository itemCappedReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest request) {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest request) {
        return itemReactiveRepository.findById(request.pathVariable("id"))
            .flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createItem(ServerRequest request) {
        return request.bodyToMono(Item.class)
            .flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.save(item), Item.class)
            );
    }

    public Mono<ServerResponse> deleteItem(ServerRequest request) {
        return itemReactiveRepository.findById(request.pathVariable("id"))
            .flatMap(item ->
                ServerResponse.ok()
                    .build(itemReactiveRepository.delete(item))
            );
    }

    public Mono<ServerResponse> updateItem(ServerRequest request) {
        return itemReactiveRepository.findById(request.pathVariable("id"))
            .flatMap(updatedItem ->
                request.bodyToMono(Item.class)
                    .flatMap(item -> {
                        updatedItem.setDescription(item.getDescription());
                        updatedItem.setPrice(item.getPrice());
                        return ServerResponse.ok()
                            .body(itemReactiveRepository.save(updatedItem), Item.class);
                    })
            ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> runtimeException(ServerRequest request) {
        throw new RuntimeException("Runtime Exception functional");
    }

    public Mono<ServerResponse> itemStream(ServerRequest request) {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_STREAM_JSON)
            .body(itemCappedReactiveRepository.findItemBy(), ItemCapped.class);
    }
}
