package cl.guaman.labhttp2server.facade.impl;

import cl.guaman.labhttp2server.builder.HTTP2ServerBuilder;
import cl.guaman.labhttp2server.facade.Server;
import cl.guaman.labhttp2server.handler.InitHTTP2ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.logging.Logger;

public class HTTP2Server implements Server, AutoCloseable {
    private static final Logger logger = Logger.getLogger(HTTP2Server.class.getName());
    private final HTTP2ServerBuilder builder;
    private final EventLoopGroup bossEventLoopGroup;
    private final EventLoopGroup workerEventLoopGroup;

    public HTTP2Server(HTTP2ServerBuilder builder) {
        this.builder = builder;
        bossEventLoopGroup = builder.getEventLoopGroupFactory().create(1);
        workerEventLoopGroup = builder.getEventLoopGroupFactory().create();
    }

    @Override
    public void start() throws InterruptedException {
        logger.info("start http2-server");
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.option(ChannelOption.SO_BACKLOG, this.builder.getSoBacklog());
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, this.builder.isSoKeepALive());
        serverBootstrap.childOption(ChannelOption.AUTO_READ, this.builder.isAutoRead());
        serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                .channel(this.builder.getServerSocketChannelFactory().create())
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new InitHTTP2ServerHandler(builder));
        Channel channel = serverBootstrap.bind(this.builder.getPort()).sync().channel();
        channel.closeFuture().sync();
    }

    public static HTTP2ServerBuilder builder() {
        return new HTTP2ServerBuilder();
    }

    @Override
    public void close() {
        logger.info("stop http2-server");
        bossEventLoopGroup.shutdownGracefully();
        workerEventLoopGroup.shutdownGracefully();
    }
}
