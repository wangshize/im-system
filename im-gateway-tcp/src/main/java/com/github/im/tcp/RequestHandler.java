package com.github.im.tcp;

import com.github.im.core.ProtocolByteBufBuilder;
import com.github.im.core.RequestCommand;
import com.github.im.protocol.AuthenticateRequest;
import com.github.im.protocol.AuthenticateResponse;
import com.github.im.tcp.dispatcher.DispatcherInstanceManager;
import com.github.im.tcp.dispatcher.DispatcherPeer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;

/**
 * @author wangsz
 * @create 2020-08-12
 **/
public class RequestHandler {

    private RequestHandler() {
    }

    static class Singleton {

        static RequestHandler instance = new RequestHandler();

    }

    public static RequestHandler getInstance() {
        return Singleton.instance;
    }

    /**
     * 将请求交给分发系统去认证请求合法性
     * @param request
     */
    public ChannelFuture authenticate(AuthenticateRequest request) {
        DispatcherInstanceManager dispatcherManager = DispatcherInstanceManager.getInstance();
        DispatcherPeer dispatcherPeer = dispatcherManager.selectOne();
        SocketChannel socketChannel = dispatcherPeer.getSocketChannel();
        ByteBuf byteBuf = ProtocolByteBufBuilder.buildByteBuf(request.toByteArray(), RequestCommand.AUTHENTICATE);
        return socketChannel.writeAndFlush(byteBuf);
    }

}
