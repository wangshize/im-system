package com.github.im.tcp;

import com.github.im.core.ImConstants;
import com.github.im.tcp.dispatcher.DispatcherInstanceManager;
import com.github.im.tcp.push.PushManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author wangsz
 * @create 2020-03-24
 **/
public class GatewayTcpServer {

    public static final int port = 8080;

    public static void main(String[] args) throws Exception {
        PushManager pushManager = new PushManager();
        pushManager.start();
        DispatcherInstanceManager dispatcherInstanceManager = DispatcherInstanceManager.getInstance();
        dispatcherInstanceManager.init();
        EventLoopGroup connectionGroup = new NioEventLoopGroup();
        EventLoopGroup ioThreadGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(connectionGroup, ioThreadGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(ImConstants.MAX_FRAME_LENGTH,
                                    /**消息总长度开始的下标*/0,
                                    /**消息总长度的值的长度，如长度域（head+body）是1000，其长度就是int 4字节*/4,
                                    /**数据包长度 - lengthFieldOffset - lengthFieldLength - 长度域的值（head+body）*/0,
                                    /**接收到的发送数据包，去除前initialBytesToStrip位*/4));
                            ch.pipeline().addLast(new GatewayTcpHandler());
                        }
                    });
            ChannelFuture channelFuture = server.bind(port).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            connectionGroup.shutdownGracefully();
            ioThreadGroup.shutdownGracefully();
        }
    }
}
