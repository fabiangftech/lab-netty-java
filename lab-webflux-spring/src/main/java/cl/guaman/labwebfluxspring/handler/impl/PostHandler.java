package cl.guaman.labwebfluxspring.handler.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class PostHandler {
    private final WebClient webClient;

    public PostHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ServerResponse> fetchPosts(ServerRequest request){
        return webClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2))
                .retry(2)
                .onErrorResume(e-> Mono.just("service unavailable"))
                .flatMap(response ->ServerResponse.ok().bodyValue(response));
    }
}
