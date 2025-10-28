package cl.guaman.labhttp2server;

import cl.guaman.labhttp2server.facade.impl.HTTP2Server;

public class Main {
    public static void main(String[] args) {
        try (HTTP2Server server = HTTP2Server.builder().build()) {
            server.start();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
