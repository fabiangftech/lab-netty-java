package cl.guaman.labhttp2server.model;

import io.netty.handler.codec.http.HttpHeaders;

public class HTTPHeaderAccessor implements HeaderAccessor {
    private final HttpHeaders headers;

    public HTTPHeaderAccessor(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public String get(String key) {
        return headers.get(key);
    }

    @Override
    public boolean contains(String key) {
        return headers.contains(key);
    }
}
