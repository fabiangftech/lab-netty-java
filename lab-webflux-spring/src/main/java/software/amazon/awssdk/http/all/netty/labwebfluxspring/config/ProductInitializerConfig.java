package software.amazon.awssdk.http.all.netty.labwebfluxspring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.http.all.netty.labwebfluxspring.model.Product;
import software.amazon.awssdk.http.all.netty.labwebfluxspring.repository.ProductRepository;

@Slf4j
@Configuration
public class ProductInitializerConfig {

    @Bean
    public CommandLineRunner initProduct(ProductRepository repository) {
        return args -> {
            repository.deleteAll()
                    .thenMany(Flux.just(
                                    new Product(null, "Laptop", 1200),
                                    new Product(null, "Headphones", 150),
                                    new Product(null, "Mouse", 25)
                            ).flatMap(repository::save)
                    )
                    .thenMany(repository.findAll())
                    .subscribe(product -> log.info(product.toString()));

        };
    }
}
