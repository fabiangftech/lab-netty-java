package software.amazon.awssdk.http.all.netty.labwebfluxspring.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.http.all.netty.labwebfluxspring.model.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

    Flux<Product> findByPriceGreaterThan(double minPrice);
}
