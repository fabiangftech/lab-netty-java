package cl.guaman.labhttp2server.model;

import cl.guaman.labhttp2server.controller.HTTPController;
import cl.guaman.labhttp2server.controller.SimpleController;
import io.netty.handler.codec.http.HttpMethod;

public class RequestHandler {
    private final HttpMethod method;

    private final String path;

    private final HTTPController controller;


    public RequestHandler(HttpMethod method, String path, HTTPController controller) {
        this.method = method;
        this.path = path;
        this.controller = controller;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HTTPController getController() {
        return controller;
    }
}
