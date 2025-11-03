package cl.guaman.labhttp2server.controller;

import cl.guaman.labhttp2server.model.HTTPResponse;

@FunctionalInterface
public interface ComplexController extends HTTPController {

    HTTPResponse execute();
}
