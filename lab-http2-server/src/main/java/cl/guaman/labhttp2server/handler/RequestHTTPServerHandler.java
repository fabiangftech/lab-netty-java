package cl.guaman.labhttp2server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class RequestHTTPServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof FullHttpRequest req)) {
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            if (HttpUtil.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER));
            }

            ByteBuf content = ctx.alloc().buffer();
            content.writeBytes(RequestHTTP2ServerHandler.RESPONSE_BYTES.duplicate());
            ByteBufUtil.writeAscii(content, " - via " + req.protocolVersion());

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.headers().setInt(CONTENT_LENGTH, content.readableBytes());

            boolean keepAlive = HttpUtil.isKeepAlive(req);

            if (keepAlive) {
                if (req.protocolVersion().equals(HTTP_1_0)) {
                    response.headers().set(CONNECTION, KEEP_ALIVE);
                }
                ctx.writeAndFlush(response);
            } else {
                response.headers().set(CONNECTION, CLOSE);
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        } finally {
            req.release();
        }
        ctx.read();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
