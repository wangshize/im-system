package com.github.im.tcp.dispatcher;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wangsz
 * @create 2020-03-29
 **/
public class DispatcherInstanceManager {

    private static List<DispatcherInstance> dispatcherInstances =
            new ArrayList<DispatcherInstance>();

    private List<SocketChannel> dispatcherChannels = new CopyOnWriteArrayList<SocketChannel>();

    static {
        //获取Dispatcher实例信息
        dispatcherInstances.add(new DispatcherInstance("localhost", "127.0.0.1", 8090));
    }

    public static DispatcherInstanceManager getInstance() {
        return Singleton.instance;
    }

    public void init() {
        for (DispatcherInstance dispatcherInstance : dispatcherInstances) {
            connectDispatcher(dispatcherInstance);
        }

    }

    private void connectDispatcher(DispatcherInstance dispatcherInstance) {
        final EventLoopGroup threadGroup = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        client.group(threadGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer("#".getBytes())));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new DispatcherClientHandler());
                    }
                });
        ChannelFuture channelFuture = client.connect(
                dispatcherInstance.getIp(), dispatcherInstance.getPort());
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    SocketChannel channel = (SocketChannel) future.channel();
                    dispatcherChannels.add(channel);
                    System.out.println("分发系统建立连接成功");
                } else {
                    future.channel().close();
                    threadGroup.shutdownGracefully();
                }
            }
        });
    }

    private static class Singleton {
        public static DispatcherInstanceManager instance = new DispatcherInstanceManager();
    }
}
