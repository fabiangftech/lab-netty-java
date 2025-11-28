package cl.guaman.labwebfluxspring.handler;

import cl.guaman.labwebfluxspring.handler.impl.HelloHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import cl.guaman.labwebfluxspring.handler.impl.PingHandler;
import cl.guaman.labwebfluxspring.handler.impl.PostHandler;
import cl.guaman.labwebfluxspring.handler.impl.TimeHandler;

@Configuration
public class MainRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(
            PingHandler pingHandler,
            HelloHandler hello,
            TimeHandler time,
            PostHandler postHandler) {
        return RouterFunctions.route()
                .GET("/ping", pingHandler::ping)
                .GET("/hello", hello::sayHello)
                .GET("/time", time::streamTime)
                .GET("/posts", postHandler::fetchPosts)
                .build();
    }
}
