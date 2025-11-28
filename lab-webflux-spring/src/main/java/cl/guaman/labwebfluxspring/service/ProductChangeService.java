package cl.guaman.labwebfluxspring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import cl.guaman.labwebfluxspring.model.Product;
import org.springframework.stereotype.Service;
import com.mongodb.MongoCommandException;

import java.util.Objects;

@Slf4j
@Service
public class ProductChangeService {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ProductChangeService(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void watchChanges() {
        reactiveMongoTemplate
                .changeStream(Product.class)
                .watchCollection("product")
                .listen()
                .doOnNext(change -> log.info(Objects.requireNonNull(change.getBody()).toString()))
                .doOnSubscribe(subscription -> 
                    log.info("Starting to watch product collection changes..."))
                .doOnCancel(() -> 
                    log.warn("Product change stream subscription was cancelled"))
                .subscribe(
                    null,
                    error -> {
                        log.error("Subscription error in product change stream: {}", error.getMessage(), error);
                    }
                );
    }
}
