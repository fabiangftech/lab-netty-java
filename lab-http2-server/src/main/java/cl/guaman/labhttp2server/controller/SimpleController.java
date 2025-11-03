package cl.guaman.labhttp2server.controller;
import cl.guaman.labhttp2server.model.HTTPResponse;

@FunctionalInterface
public interface SimpleController extends HTTPController{

    HTTPResponse execute();
}
