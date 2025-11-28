package software.amazon.awssdk.http.all.netty.labwebfluxspring.handler.impl;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TimeHandler {

    public Mono<ServerResponse> streamTime(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(Flux.interval(Duration.ofSeconds(1)).map(tick -> "Current time: " + LocalDateTime.now()), String.class);
    }
}
