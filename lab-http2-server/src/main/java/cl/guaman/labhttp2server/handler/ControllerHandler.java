package cl.guaman.labhttp2server.handler;

import cl.guaman.labhttp2server.model.HTTPRequest;
import cl.guaman.labhttp2server.model.HTTPResponse;

@FunctionalInterface
public interface ControllerHandler {
    HTTPResponse handle(HTTPRequest request);
}
