package cl.guaman.labhttp2server.model;

import cl.guaman.labhttp2server.handler.ControllerHandler;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InternalRouterTest {

     @Test
     void testGetRoutesWithParams() {
         InternalRouter router = new InternalRouter();

         ControllerHandler path1Handler = request -> HTTPResponse.builder().build();
         ControllerHandler path1IdHandler = request -> HTTPResponse.builder().build();
         ControllerHandler path1IdPath2Handler = request -> HTTPResponse.builder().build();

         router.add(HttpMethod.GET, "/path-1", path1Handler);
         router.add(HttpMethod.GET, "/path-1/:id", path1IdHandler);
         router.add(HttpMethod.GET, "/path-1/:id/path2", path1IdPath2Handler);

         // GET /path-1
         InternalRouter.Match m1 = router.match(HttpMethod.GET, "/path-1");
         assertNotNull(m1);
         assertSame(path1Handler, m1.handler());
         assertTrue(m1.pathParams().isEmpty());

         // GET /path-1/
         InternalRouter.Match m1b = router.match(HttpMethod.GET, "/path-1/");
         assertNotNull(m1b);
         assertSame(path1Handler, m1b.handler());
         assertTrue(m1b.pathParams().isEmpty());

         // GET /path-1/123
         InternalRouter.Match m2 = router.match(HttpMethod.GET, "/path-1/123");
         assertNotNull(m2);
         assertSame(path1IdHandler, m2.handler());
         assertEquals(Map.of("id", "123"), m2.pathParams());

         // GET /path-1/999/path2
         InternalRouter.Match m3 = router.match(HttpMethod.GET, "/path-1/999/path2");
         assertNotNull(m3);
         assertSame(path1IdPath2Handler, m3.handler());
         assertEquals(Map.of("id", "999"), m3.pathParams());
     }

     @Test
     void testDifferentMethodsSamePath() {
         InternalRouter router = new InternalRouter();

         ControllerHandler handler = request -> HTTPResponse.builder().build();
         ControllerHandler postHandler = request -> HTTPResponse.builder().build();

         router.add(HttpMethod.GET, "/resource", handler);
         router.add(HttpMethod.POST, "/resource", postHandler);

         InternalRouter.Match getMatch = router.match(HttpMethod.GET, "/resource");
         assertNotNull(getMatch);
         assertSame(handler, getMatch.handler());

         InternalRouter.Match postMatch = router.match(HttpMethod.POST, "/resource");
         assertNotNull(postMatch);
         assertSame(postHandler, postMatch.handler());

         assertNull(router.match(HttpMethod.PUT, "/resource"));
     }

     @Test
     void testWildcardRoute() {
         InternalRouter router = new InternalRouter();
         ControllerHandler filesHandler = request -> HTTPResponse.builder().build();
         router.add(HttpMethod.GET, "/files/*path", filesHandler);

         InternalRouter.Match m1 = router.match(HttpMethod.GET, "/files/a/b/c.txt");
         assertNotNull(m1);
         assertSame(filesHandler, m1.handler());
         assertEquals(Map.of("path", "a/b/c.txt"), m1.pathParams());

         InternalRouter.Match m2 = router.match(HttpMethod.GET, "/files/onlyOne");
         assertNotNull(m2);
         assertSame(filesHandler, m2.handler());
         assertEquals(Map.of("path", "onlyOne"), m2.pathParams());
     }

     @Test
     void testNoMatch() {
         InternalRouter router = new InternalRouter();
         ControllerHandler handler = request -> HTTPResponse.builder().build();

         router.add(HttpMethod.GET, "/path-1/:id/path2", handler);

         assertNull(router.match(HttpMethod.GET, "/path-1"));
         assertNull(router.match(HttpMethod.GET, "/path-1/123/other"));
         assertNull(router.match(HttpMethod.GET, "/other"));
     }

     @Test
     void testConflictingParamsSameLevelThrows() {
         InternalRouter router = new InternalRouter();
         ControllerHandler h1 = request -> HTTPResponse.builder().build();
         ControllerHandler h2 = request -> HTTPResponse.builder().build();

         router.add(HttpMethod.GET, "/users/:id", h1);

         assertThrows(IllegalStateException.class, () ->
                 router.add(HttpMethod.GET, "/users/:userId", h2)
         );
     }

     @Test
     void testWildcardMustBeLastSegment() {
         InternalRouter router = new InternalRouter();

         ControllerHandler h1 = request -> HTTPResponse.builder().build();

         assertThrows(IllegalArgumentException.class, () ->
                 router.add(HttpMethod.GET, "/files/*path/extra", h1)
         );
     }
}
