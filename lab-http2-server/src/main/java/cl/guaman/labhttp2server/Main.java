package cl.guaman.labhttp2server;

import cl.guaman.labhttp2server.controller.NoParamController;
import cl.guaman.labhttp2server.controller.WithParamController;
import cl.guaman.labhttp2server.facade.impl.HTTP2Server;
import cl.guaman.labhttp2server.model.HTTPRequest;
import cl.guaman.labhttp2server.model.HTTPResponse;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        try (HTTP2Server server = HTTP2Server.builder()
                .port(8081)
                .maxContentLength(1024 * 1024)
                .autoRead(false)
                .GET("/", () -> HTTPResponse.builder()
                        .ok()
                        .setContentTypeTextPlain()
                        .body("lab-http2-server".getBytes(StandardCharsets.UTF_8))
                        .build())
                .build()) {
            server.start();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
