package com.github.im.sdk;

import com.github.im.core.ImConstants;
import com.github.im.core.ProtocolByteBufBuilder;
import com.github.im.core.RequestCommand;
import com.github.im.core.UserRequest;
import com.github.im.protocol.AuthenticateRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(ImConstants.MAX_FRAME_LENGTH,
                                0, 4, 0, 4));
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
    public void authenticate(String uid, String token) throws InterruptedException {
        AuthenticateRequest request = AuthenticateRequest.newBuilder()
                .setUid(uid)
                .setToken(token)
                .setTimestamp(System.currentTimeMillis())
                .build();
        byte[] body = request.toByteArray();
        ByteBuf requestBuffer = ProtocolByteBufBuilder.buildByteBuf(body, RequestCommand.AUTHENTICATE);
        System.out.println("向TCP接入系统发起用户认证");
        ChannelFuture future = channel.writeAndFlush(requestBuffer);
    }

    /**
     * 发送消息
     * @param message
     */
    public void send(String userId, String message) {
        UserRequest request = new UserRequest();
        request.setUserId(userId);
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
