package cl.guaman.labhttp2server.factory.impl;


import cl.guaman.labhttp2server.factory.Factory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.uring.IoUring;
import io.netty.channel.uring.IoUringIoHandler;

import java.util.logging.Logger;

public class EventLoopGroupFactory implements Factory<Integer, EventLoopGroup> {
    private static final Logger logger = Logger.getLogger(EventLoopGroupFactory.class.getName());

    @Override
    public EventLoopGroup create(Integer threads) {
        if (IoUring.isAvailable()) {
            return threads == null ?
                    new MultiThreadIoEventLoopGroup(IoUringIoHandler.newFactory()) :
                    new MultiThreadIoEventLoopGroup(threads, IoUringIoHandler.newFactory());
        } else if (Epoll.isAvailable()) {
            return threads == null ?
                    new MultiThreadIoEventLoopGroup(EpollIoHandler.newFactory()) :
                    new MultiThreadIoEventLoopGroup(threads, EpollIoHandler.newFactory());
        } else if (KQueue.isAvailable()) {
            return threads == null ?
                    new MultiThreadIoEventLoopGroup(KQueueIoHandler.newFactory()) :
                    new MultiThreadIoEventLoopGroup(threads, KQueueIoHandler.newFactory());
        }
        return threads == null ?
                new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory()) :
                new MultiThreadIoEventLoopGroup(threads, NioIoHandler.newFactory());
    }
}

