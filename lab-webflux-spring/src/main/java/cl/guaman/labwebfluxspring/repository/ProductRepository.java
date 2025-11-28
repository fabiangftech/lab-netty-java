package cl.guaman.labwebfluxspring.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import cl.guaman.labwebfluxspring.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

    Flux<Product> findByPriceGreaterThan(double minPrice);
}
