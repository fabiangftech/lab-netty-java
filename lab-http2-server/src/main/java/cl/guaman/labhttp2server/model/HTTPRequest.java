package cl.guaman.labhttp2server.model;

import io.netty.handler.codec.http.FullHttpRequest;

public class HTTPRequest {

    private final HTTPVersion version;
    private FullHttpRequest fullHttpRequest;

    public HTTPRequest(FullHttpRequest fullHttpRequest) {
        this.version = HTTPVersion.V1_1;
        this.fullHttpRequest = fullHttpRequest;
    }

    public String getHeader(String key) {
        if (HTTPVersion.V1_1.equals(version)) {
            return fullHttpRequest.headers().get(key);
        }
        throw new IllegalArgumentException(String.format("version not supported %s", version));
    }

    public byte[] getBody() {
        if (HTTPVersion.V1_1.equals(version)) {
            return fullHttpRequest.content().array();
        }
        throw new IllegalArgumentException(String.format("version not supported %s", version));
    }
}
