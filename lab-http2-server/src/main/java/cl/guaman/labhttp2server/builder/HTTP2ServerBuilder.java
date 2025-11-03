package cl.guaman.labhttp2server.builder;

import cl.guaman.labhttp2server.controller.ComplexController;
import cl.guaman.labhttp2server.controller.HTTPController;
import cl.guaman.labhttp2server.controller.SimpleController;
import cl.guaman.labhttp2server.facade.impl.HTTP2Server;
import cl.guaman.labhttp2server.factory.Factory;
import cl.guaman.labhttp2server.factory.impl.EventLoopGroupFactory;
import cl.guaman.labhttp2server.factory.impl.ServerSocketChannelFactory;
import cl.guaman.labhttp2server.model.RequestHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.handler.codec.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

public class HTTP2ServerBuilder {
    private final Factory<Integer, EventLoopGroup> eventLoopGroupFactory = new EventLoopGroupFactory();
    private final Factory<Void, Class<? extends ServerChannel>> serverSocketChannelFactory = new ServerSocketChannelFactory();
    private int port = 8080;
    private int maxContentLength = 1024 * 256;
    private final List<RequestHandler> requestHandlers = new ArrayList<>();

    public HTTP2ServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public HTTP2ServerBuilder maxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this;
    }


    public HTTP2ServerBuilder GET(String path, SimpleController controller) {
        requestHandlers.add(new RequestHandler(HttpMethod.GET, path, controller));
        return this;
    }

    public HTTP2ServerBuilder GET(String path, ComplexController controller) {
        requestHandlers.add(new RequestHandler(HttpMethod.GET, path, controller));
        return this;
    }

    public HTTP2ServerBuilder POST(String path, SimpleController controller) {
        requestHandlers.add(new RequestHandler(HttpMethod.POST, path, controller));
        return this;
    }

    public HTTP2ServerBuilder PUT(String path, SimpleController controller) {
        requestHandlers.add(new RequestHandler(HttpMethod.PUT, path, controller));
        return this;
    }

    public HTTP2ServerBuilder PATCH(String path, SimpleController controller) {
        requestHandlers.add(new RequestHandler(HttpMethod.PATCH, path, controller));
        return this;
    }

    public HTTP2ServerBuilder DELETE(String path, SimpleController controller) {
        requestHandlers.add(new RequestHandler(HttpMethod.DELETE, path, controller));
        return this;
    }

    public HTTP2ServerBuilder HEAD(String path, SimpleController controller) {
        requestHandlers.add(new RequestHandler(HttpMethod.HEAD, path, controller));
        return this;
    }

    public HTTP2Server build() {
        return new HTTP2Server(this);
    }

    public Factory<Integer, EventLoopGroup> getEventLoopGroupFactory() {
        return eventLoopGroupFactory;
    }

    public Factory<Void, Class<? extends ServerChannel>> getServerSocketChannelFactory() {
        return serverSocketChannelFactory;
    }

    public int getPort() {
        return port;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }
}
