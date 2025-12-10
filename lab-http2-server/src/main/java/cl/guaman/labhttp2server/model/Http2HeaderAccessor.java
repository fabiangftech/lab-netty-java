package cl.guaman.labhttp2server.model;

import io.netty.handler.codec.http2.Http2Headers;

public class Http2HeaderAccessor implements HeaderAccessor {
    private final Http2Headers headers;
   public Http2HeaderAccessor(Http2Headers headers) { this.headers = headers; }

    @Override
    public String get(String key) {
        CharSequence value = headers.get(key);
        return value == null ? null : value.toString();
    }

    @Override
    public boolean contains(String key) { return headers.contains(key); }
}