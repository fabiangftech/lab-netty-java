package cl.guaman.labhttp2server.factory.impl;

import cl.guaman.labhttp2server.factory.Factory;
import cl.guaman.labhttp2server.handler.RequestHTTP2ServerHandler;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec;
import io.netty.util.AsciiString;

public class UpgradeCodecFFactory implements Factory<Void, HttpServerUpgradeHandler.UpgradeCodecFactory> {
    @Override
    public HttpServerUpgradeHandler.UpgradeCodecFactory create(Void input) {
        return protocol -> {
            if (AsciiString.contentEquals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME, protocol)) {
                return new Http2ServerUpgradeCodec(
                        Http2FrameCodecBuilder.forServer().build(),
                        new Http2MultiplexHandler(new RequestHTTP2ServerHandler())
                );
            } else {
                return null;
            }
        };
    }
}

