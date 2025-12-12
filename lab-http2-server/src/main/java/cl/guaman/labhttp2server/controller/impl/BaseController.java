package cl.guaman.labhttp2server.controller.impl;

import cl.guaman.labhttp2server.controller.NoParamController;
import cl.guaman.labhttp2server.model.HTTPResponse;

import java.nio.charset.StandardCharsets;

public class BaseController {
    public static NoParamController notFound = () -> HTTPResponse.builder()
            .status(404)
            .setContentTypeTextPlain()
            .body("Not found".getBytes(StandardCharsets.UTF_8))
            .build();

    public static NoParamController internalServerError = () -> HTTPResponse.builder()
            .status(500)
            .setContentTypeTextPlain()
            .body("Internal server error".getBytes(StandardCharsets.UTF_8))
            .build();
}
