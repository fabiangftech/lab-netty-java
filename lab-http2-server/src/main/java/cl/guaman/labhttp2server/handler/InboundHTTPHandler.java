package cl.guaman.labhttp2server.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class InboundHTTPHandler extends SimpleChannelInboundHandler<HttpMessage> {
    public final int maxContentLength;

    public InboundHTTPHandler(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read(); // kick off first read when auto-read is disabled
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpMessage httpMessage) throws Exception {
        ChannelPipeline pipeline = channelHandlerContext.pipeline();
        pipeline.addAfter(channelHandlerContext.name(), null, new RequestHTTPServerHandler());
        pipeline.replace(this, null, new HttpObjectAggregator(maxContentLength));
        channelHandlerContext.fireChannelRead(ReferenceCountUtil.retain(httpMessage));
    }
}
