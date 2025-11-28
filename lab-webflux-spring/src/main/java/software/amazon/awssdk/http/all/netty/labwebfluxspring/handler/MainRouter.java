package software.amazon.awssdk.http.all.netty.labwebfluxspring.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import software.amazon.awssdk.http.all.netty.labwebfluxspring.handler.impl.HelloHandler;
import software.amazon.awssdk.http.all.netty.labwebfluxspring.handler.impl.PingHandler;
import software.amazon.awssdk.http.all.netty.labwebfluxspring.handler.impl.PostHandler;
import software.amazon.awssdk.http.all.netty.labwebfluxspring.handler.impl.TimeHandler;

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
