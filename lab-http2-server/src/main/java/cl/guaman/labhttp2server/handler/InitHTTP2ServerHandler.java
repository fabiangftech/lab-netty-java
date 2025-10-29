package cl.guaman.labhttp2server.handler;

import cl.guaman.labhttp2server.builder.HTTP2ServerBuilder;
import cl.guaman.labhttp2server.factory.Factory;
import cl.guaman.labhttp2server.factory.impl.SimpleChannelFactory;
import cl.guaman.labhttp2server.factory.impl.UpgradeCodecFFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;

public class InitHTTP2ServerHandler extends ChannelInitializer<SocketChannel> {
    private final HTTP2ServerBuilder builder;
    protected final Factory<Void, HttpServerUpgradeHandler.UpgradeCodecFactory> upgradeCodecFFactory = new UpgradeCodecFFactory();
    protected final Factory<Integer, SimpleChannelInboundHandler<HttpMessage>> simpleChannelFactory = new SimpleChannelFactory();

    public InitHTTP2ServerHandler(HTTP2ServerBuilder builder) {
        this.builder = builder;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //todo need if with ssl
        initWithoutSSL(socketChannel);
    }

    private void initWithoutSSL(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        HttpServerCodec httpServerCodec = new HttpServerCodec();
        pipeline.addLast(httpServerCodec);
        pipeline.addLast(new HttpServerUpgradeHandler(httpServerCodec, upgradeCodecFFactory.create()));
        pipeline.addLast(simpleChannelFactory.create(builder.getMaxContentLength()));
    }
}
