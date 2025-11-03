package cl.guaman.labhttp2server.controller;
import cl.guaman.labhttp2server.model.HTTPResponse;

@FunctionalInterface
public interface NoParamController extends HTTPController{

    HTTPResponse execute();
}
