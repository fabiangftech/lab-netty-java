package cl.guaman.labhttp2server.model;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;

public class HTTPRequest {

    private final HTTPVersion version;
    private HttpHeaders headers;
    private Http2Headers http2Headers;
    private final ByteBuf body;

    public HTTPRequest(FullHttpRequest fullHttpRequest) {
        this.version = HTTPVersion.V1_1;
        this.headers = fullHttpRequest.headers();
        this.body = fullHttpRequest.content();
    }

    public HTTPRequest(Http2HeadersFrame headersFrame, ByteBuf body) {
        this.version = HTTPVersion.V2_0;
        this.http2Headers = headersFrame.headers();
        this.body = body;
    }

    public String getHeader(String key) {
        if (HTTPVersion.V1_1.equals(version)) {
            return headers.get(key);
        } else if (HTTPVersion.V2_0.equals(version)) {
            return http2Headers.get(key).toString();
        }
        throw new IllegalArgumentException(String.format("version not supported %s", version));
    }

    public ByteBuf getBody() {
        return body;
    }
}
