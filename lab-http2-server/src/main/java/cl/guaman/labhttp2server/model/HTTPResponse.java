package cl.guaman.labhttp2server.model;

import cl.guaman.labhttp2server.builder.HTTPResponseBuilder;

import java.util.Map;

public class HTTPResponse {

    private final int status;
    private final Map<String, String> headers;
    private final byte[] body;

    public HTTPResponse(HTTPResponseBuilder builder) {
        this.status = builder.getStatus();
        this.headers = builder.getHeaders();
        this.body = builder.getBody();
    }

    public static HTTPResponseBuilder builder() {
        return new HTTPResponseBuilder();
    }

    public int getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
