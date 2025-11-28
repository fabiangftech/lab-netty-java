package cl.guaman.labwebfluxspring.handler.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PingHandler {

    public Mono<ServerResponse> ping(ServerRequest request) {
        return ServerResponse.ok().bodyValue("pong");
    }
}
