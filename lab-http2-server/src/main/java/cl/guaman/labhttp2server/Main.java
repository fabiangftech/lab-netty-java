package cl.guaman.labhttp2server;

import cl.guaman.labhttp2server.facade.impl.HTTP2Server;
import cl.guaman.labhttp2server.model.HTTPResponse;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try (HTTP2Server server = HTTP2Server.builder()
                .port(8080)
                .maxContentLength(1024 * 1024)
                .GET("/", ()-> HTTPResponse.builder()
                        .status(200)
                        .header("Content-Type", "text/plain")
                        .body("lab-http2-server".getBytes(StandardCharsets.UTF_8))
                        .build())
                .build()) {
            server.start();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
