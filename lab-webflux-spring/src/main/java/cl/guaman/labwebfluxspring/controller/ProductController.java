package cl.guaman.labwebfluxspring.controller;

import cl.guaman.labwebfluxspring.model.Product;
import cl.guaman.labwebfluxspring.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public Flux<Product> getAll() {
        return productRepository.findAll();
    }

    @PostMapping
    public Mono<Product> save(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping("/test/add")
    public Mono<Product> testAddProduct() {
        return save(new Product(null, "Product " + LocalDateTime.now(), 2000));
    }
}
