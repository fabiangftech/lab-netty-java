package cl.guaman.labhttp2server.builder;

import cl.guaman.labhttp2server.model.HTTPResponse;

import java.util.Map;

public class HTTPResponseBuilder {

    private int status;
    private Map<String, String> headers;
    private byte[] body;

    public HTTPResponseBuilder() {
    }

    public HTTPResponseBuilder status(int status) {
        this.status = status;
        return this;
    }

    public HTTPResponseBuilder header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HTTPResponseBuilder body(byte[] body) {
        this.body = body;
        return this;
    }

    public HTTPResponse build() {
        return new HTTPResponse(this);
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
