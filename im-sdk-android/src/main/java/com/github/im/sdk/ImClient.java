package com.github.im.sdk;

import com.github.im.core.RequestCommand;
import com.github.im.core.UserRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author wangsz
 * @create 2020-03-25
 **/
public class ImClient {

    private Bootstrap client;

    private EventLoopGroup threadGroup;
    /**
     * 客户端和和接入系统的长链接
     */
    private SocketChannel channel;

    public static final int port = 8080;
    public static final String host = "127.0.0.1";

    /**
     * 与目标机器建立连接
     * @param host
     * @param port
     * @throws Exception
     */
    public void connect(String host, int port) throws Exception {
        this.threadGroup = new NioEventLoopGroup();
        this.client = new Bootstrap();
        this.client.group(threadGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer("#".getBytes())));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ImClientHandler());
                    }
                });
        ChannelFuture channelFuture = client.connect(host, port);
        System.out.println("向TCP接入系统发起连接");
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    channel = (SocketChannel) future.channel();
                    System.out.println("TCP接入系统建立连接成功");
                } else {
                    future.channel().close();
                    threadGroup.shutdownGracefully();
                }
            }
        });
    }

    /**
     * 用户认证
     */
    public void authenticate(String userId, String token) {
        UserRequest request = new UserRequest();
        request.setCommand(RequestCommand.AUTHENTICATE);
        request.setUserId(userId);
        request.setToken(token);
        ByteBuf byteBuf = request.toByteBuf();
        channel.writeAndFlush(byteBuf);
        System.out.println("向TCP接入系统发起用户认证");
    }

    /**
     * 发送消息
     * @param message
     */
    public void send(String userId, String message) {
        UserRequest request = new UserRequest();
        request.setUserId(userId);
        request.setMessage(message);
        request.setCommand(RequestCommand.MESSAGE);
        this.channel.writeAndFlush(request.toByteBuf());
        System.out.println("向TCP接入系统发起消息，推送给test002");
    }

    /**
     * 关闭连接
     */
    public void close() {
        this.channel.close();
        this.threadGroup.shutdownGracefully();
    }
}
