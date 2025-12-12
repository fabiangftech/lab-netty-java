package cl.guaman.labhttp2server.builder;

import cl.guaman.labhttp2server.controller.HTTPController;
import cl.guaman.labhttp2server.controller.WithParamController;
import cl.guaman.labhttp2server.controller.NoParamController;
import cl.guaman.labhttp2server.facade.impl.HTTP2Server;
import cl.guaman.labhttp2server.factory.Factory;
import cl.guaman.labhttp2server.factory.impl.EventLoopGroupFactory;
import cl.guaman.labhttp2server.factory.impl.ServerSocketChannelFactory;
import cl.guaman.labhttp2server.model.InternalRouter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.handler.codec.http.HttpMethod;

public class HTTP2ServerBuilder {
    private final Factory<Integer, EventLoopGroup> eventLoopGroupFactory = new EventLoopGroupFactory();
    private final Factory<Void, Class<? extends ServerChannel>> serverSocketChannelFactory = new ServerSocketChannelFactory();
    private final InternalRouter router = new InternalRouter();
    private int port = 8080;
    private int maxContentLength = 1024 * 256;
    private int soBacklog = 4096;
    private boolean soKeepALive = true;
    private boolean autoRead = true;

    public HTTP2ServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public HTTP2ServerBuilder maxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this;
    }

    public HTTP2ServerBuilder soBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
        return this;
    }

    public HTTP2ServerBuilder soKeepALive(boolean soKeepALive) {
        this.soKeepALive = soKeepALive;
        return this;
    }

    public HTTP2ServerBuilder autoRead(boolean autoRead) {
        this.autoRead = autoRead;
        return this;
    }


    public HTTP2ServerBuilder GET(String path, NoParamController controller) {
        router.add(HttpMethod.GET, path, controller);
        return this;
    }

    public HTTP2ServerBuilder GET(String path, WithParamController controller) {
        router.add(HttpMethod.GET, path, controller);
        return this;
    }

    public HTTP2ServerBuilder POST(String path, NoParamController controller) {
        router.add(HttpMethod.POST, path, controller);
        return this;
    }

    public HTTP2ServerBuilder POST(String path, WithParamController controller) {
        router.add(HttpMethod.POST, path, controller);
        return this;
    }

    public HTTP2ServerBuilder PUT(String path, NoParamController controller) {
        router.add(HttpMethod.PUT, path, controller);
        return this;
    }

    public HTTP2ServerBuilder PUT(String path, WithParamController controller) {
        router.add(HttpMethod.PUT, path, controller);
        return this;
    }

    public HTTP2ServerBuilder PATCH(String path, NoParamController controller) {
        router.add(HttpMethod.PATCH, path, controller);
        return this;
    }

    public HTTP2ServerBuilder PATCH(String path, WithParamController controller) {
        router.add(HttpMethod.PATCH, path, controller);
        return this;
    }

    public HTTP2ServerBuilder DELETE(String path, NoParamController controller) {
        router.add(HttpMethod.DELETE, path, controller);
        return this;
    }

    public HTTP2ServerBuilder DELETE(String path, WithParamController controller) {
        router.add(HttpMethod.DELETE, path, controller);
        return this;
    }

    public HTTP2ServerBuilder HEAD(String path, NoParamController controller) {
        router.add(HttpMethod.HEAD, path, controller);
        return this;
    }

    public HTTP2ServerBuilder HEAD(String path, WithParamController controller) {
        router.add(HttpMethod.HEAD, path, controller);
        return this;
    }
    public HTTP2ServerBuilder OPTIONS(String path, NoParamController controller) {
        router.add(HttpMethod.OPTIONS, path, controller);
        return this;
    }

    public HTTP2ServerBuilder OPTIONS(String path, WithParamController controller) {
        router.add(HttpMethod.OPTIONS, path, controller);
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

    public int getSoBacklog() {
        return soBacklog;
    }

    public boolean isSoKeepALive() {
        return soKeepALive;
    }

    public boolean isAutoRead() {
        return autoRead;
    }
}
