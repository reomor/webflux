package com.example.fluxpaxg.controller.v1;

import com.example.fluxpaxg.docs.ItemCapped;
import com.example.fluxpaxg.repository.ItemCappedReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ItemStreamController {

    public static final String ITEM_STREAM_V1 = "/v1/stream/items";

    private final ItemCappedReactiveRepository itemCappedReactiveRepository;

    @GetMapping(
        value = ITEM_STREAM_V1,
        produces = MediaType.APPLICATION_STREAM_JSON_VALUE
    )
    public Flux<ItemCapped> getItemsStream() {
        return itemCappedReactiveRepository.findItemBy();
    }
}
