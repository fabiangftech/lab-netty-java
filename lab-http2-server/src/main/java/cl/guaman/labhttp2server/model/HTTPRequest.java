package cl.guaman.labhttp2server.model;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.Http2HeadersFrame;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HTTPRequest {

    private static final String URI_SCHEME_SEPARATOR = "://";
    private final HTTPVersion version;
    private final String uri;
    private final String path;
    private final Map<String, List<String>> queryParameters;
    private final HeaderAccessor headers;

    private final ByteBuf body;

    public HTTPRequest(FullHttpRequest fullHttpRequest) {
        this.version = HTTPVersion.V1_1;
        this.uri = fullHttpRequest.uri();
        QueryStringDecoder decoder = new QueryStringDecoder(this.uri);
        this.path = decoder.path();
        this.queryParameters = decoder.parameters();
        this.headers = new Http1HeaderAccessor(fullHttpRequest.headers());
        this.body = fullHttpRequest.content().retain();
    }

    public HTTPRequest(Http2HeadersFrame headersFrame, ByteBuf body) {
        this.version = HTTPVersion.V2_0;
        this.uri = headersFrame.headers().scheme().toString()
                .concat(URI_SCHEME_SEPARATOR)
                .concat(headersFrame.headers().authority().toString())
                .concat(headersFrame.headers().path().toString());
        QueryStringDecoder decoder = new QueryStringDecoder(this.uri);
        this.path = decoder.path();
        this.queryParameters = decoder.parameters();
        this.headers = new Http2HeaderAccessor(headersFrame.headers());
        this.body = body.retain();
    }

    public HTTPVersion getVersion() {
        return version;
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

    public List<String> getQueryParameters(String key) {
        return queryParameters.get(key);
    }

    public Optional<String> getFirstQueryParametersByKey(String key) {
        if (queryParameters.containsKey(key)) {
            return Optional.of(queryParameters.get(key).get(0));
        }
        return Optional.empty();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public boolean containsHeader(String name) {
        return headers.contains(name);
    }

    public ByteBuf getBody() {
        return body;
    }
}
