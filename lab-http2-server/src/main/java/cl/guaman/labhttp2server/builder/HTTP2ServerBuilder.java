package cl.guaman.labhttp2server.builder;

import cl.guaman.labhttp2server.facade.impl.HTTP2Server;
import cl.guaman.labhttp2server.factory.Factory;
import cl.guaman.labhttp2server.factory.impl.EventLoopGroupFactory;
import cl.guaman.labhttp2server.factory.impl.ServerSocketChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public class HTTP2ServerBuilder {
    private final Factory<Integer, EventLoopGroup> eventLoopGroupFactory = new EventLoopGroupFactory();
    private final Factory<Void, Class<? extends ServerChannel>> serverSocketChannelFactory = new ServerSocketChannelFactory();

    private int port = 8080;
    private int maxContentLength = 1024 * 256;

    public HTTP2ServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public HTTP2ServerBuilder maxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
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
