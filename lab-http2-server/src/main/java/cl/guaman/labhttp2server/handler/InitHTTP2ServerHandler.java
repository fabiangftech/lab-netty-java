package cl.guaman.labhttp2server.handler;

import cl.guaman.labhttp2server.builder.HTTP2ServerBuilder;
import cl.guaman.labhttp2server.factory.impl.HTTP2UpgradeCodecFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler.UpgradeCodecFactory;

public class InitHTTP2ServerHandler extends ChannelInitializer<SocketChannel> {

    private final InboundHTTPHandler inboundHTTPHandler;
    private final UpgradeCodecFactory http2UpgradeCodecFactory;
    public InitHTTP2ServerHandler(HTTP2ServerBuilder builder) {
        this.inboundHTTPHandler = new InboundHTTPHandler(builder.getMaxContentLength());
        this.http2UpgradeCodecFactory=new HTTP2UpgradeCodecFactory();
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
        pipeline.addLast(new HttpServerUpgradeHandler(httpServerCodec, http2UpgradeCodecFactory));
        pipeline.addLast(inboundHTTPHandler);
    }
}
