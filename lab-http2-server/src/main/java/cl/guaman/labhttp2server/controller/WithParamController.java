package cl.guaman.labhttp2server.controller;

import cl.guaman.labhttp2server.model.HTTPRequest;
import cl.guaman.labhttp2server.model.HTTPResponse;

@FunctionalInterface
public interface WithParamController extends HTTPController {

    HTTPResponse execute(HTTPRequest request);
}
