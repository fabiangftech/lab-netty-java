package cl.guaman.labhttp2server.factory.impl;

import cl.guaman.labhttp2server.factory.Factory;
import cl.guaman.labhttp2server.handler.RequestHTTPServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.ReferenceCountUtil;

public class SimpleChannelFactory implements Factory<Integer, SimpleChannelInboundHandler<HttpMessage>> {
    @Override
    public SimpleChannelInboundHandler<HttpMessage> create(Integer maxContentLength) {
        return new SimpleChannelInboundHandler<>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addAfter(ctx.name(), null, new RequestHTTPServerHandler());
                pipeline.replace(this, null, new HttpObjectAggregator(maxContentLength));
                ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
            }
        };
    }
}
