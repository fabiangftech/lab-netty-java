package cl.guaman.labhttp2server.factory.impl;

import cl.guaman.labhttp2server.factory.Factory;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.uring.IoUring;
import io.netty.channel.uring.IoUringServerSocketChannel;

public class ServerSocketChannelFactory implements Factory<Void, Class<? extends ServerChannel>> {

    public Class<? extends ServerChannel> create(Void input) {
        if (IoUring.isAvailable()) {
            return IoUringServerSocketChannel.class;
        } else if (Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        } else if (KQueue.isAvailable()) {
            return KQueueServerSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }
}