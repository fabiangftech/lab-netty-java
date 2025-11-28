package cl.guaman.labhttp2server.model;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;

import java.util.List;
import java.util.Map;

public class HTTPRequest {
    private final HTTPVersion version;
    private final String uri;
    private final String path;
    private final Map<String, List<String>> queryParameters;
    private HttpHeaders headers;
    private Http2Headers http2Headers;
    private final ByteBuf body;

    public HTTPRequest(FullHttpRequest fullHttpRequest) {
        this.version = HTTPVersion.V1_1;
        this.uri = fullHttpRequest.uri();
        QueryStringDecoder decoder = new QueryStringDecoder(this.uri);
        this.path = decoder.path();
        this.queryParameters = decoder.parameters();
        this.headers = fullHttpRequest.headers();
        this.body = fullHttpRequest.content();
    }

    public HTTPRequest(Http2HeadersFrame headersFrame, ByteBuf body) {
        this.version = HTTPVersion.V2_0;
        this.uri = headersFrame.headers().scheme().toString()
                .concat("://")
                .concat(headersFrame.headers().authority().toString())
                .concat(headersFrame.headers().path().toString());
        QueryStringDecoder decoder = new QueryStringDecoder(this.uri);
        this.path = decoder.path();
        this.queryParameters = decoder.parameters();
        this.http2Headers = headersFrame.headers();
        this.body = body;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
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
