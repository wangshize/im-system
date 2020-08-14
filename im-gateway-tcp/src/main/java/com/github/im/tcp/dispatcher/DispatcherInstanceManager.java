package com.github.im.tcp.dispatcher;

import com.github.im.core.ImConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 请求分发系统管理
 * 接入系统和分发系统双向持有对方的所有节点长链接
 * @author wangsz
 * @create 2020-03-29
 **/
public class DispatcherInstanceManager {

    private static List<DispatcherAddress> dispatcherInstances =
            new ArrayList<DispatcherAddress>();

    private List<DispatcherPeer> dispatchers = new CopyOnWriteArrayList<DispatcherPeer>();

    static {
        //获取Dispatcher分发系统的实例信息
        dispatcherInstances.add(new DispatcherAddress("localhost", "127.0.0.1", 8090));
    }

    public static DispatcherInstanceManager getInstance() {
        return Singleton.instance;
    }

    public void init() {
        //接入系统和分发系统建立长链接
        for (DispatcherAddress dispatcherInstance : dispatcherInstances) {
            connectDispatcher(dispatcherInstance);
        }

    }

    public List<DispatcherPeer> getDispatchers() {
        return dispatchers;
    }

    public DispatcherPeer selectOne() {
        Random random = new Random();
        int index = random.nextInt(dispatchers.size());
        return dispatchers.get(index);
    }

    private void connectDispatcher(DispatcherAddress dispatcherInstance) {
        final EventLoopGroup threadGroup = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        client.group(threadGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(ImConstants.MAX_FRAME_LENGTH,
                                0,4,0,4));
                        ch.pipeline().addLast(new DispatcherClientHandler());
                    }
                });
        ChannelFuture channelFuture = client.connect(
                dispatcherInstance.getIp(), dispatcherInstance.getPort());
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    SocketChannel channel = (SocketChannel) future.channel();
                    dispatchers.add(new DispatcherPeer(channel));
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
