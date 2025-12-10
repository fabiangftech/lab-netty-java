package cl.guaman.labhttp2server.model;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class HTTPRequestTest {
    @Test
    void testConstructHTTP1() {
        String uri = "http://localhost:8080/users/1?admin=true";
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.GET,
                uri,
                Unpooled.EMPTY_BUFFER);
        HTTPRequest request = new HTTPRequest(fullHttpRequest);
        Optional<String> isAdmin = request.getFirstQueryParametersByKey("admin");
        Assertions.assertNotNull(request);
        Assertions.assertEquals(uri, request.getUri());
        Assertions.assertEquals("true", isAdmin.orElse("false"));
        Assertions.assertEquals("http://localhost:8080/users/1", request.getPath());
    }
}
