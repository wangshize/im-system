package com.github.im.tcp.dispatcher;

import com.github.im.core.DataPackage;
import com.github.im.core.ImConstants;
import com.github.im.core.ProtocolByteBufBuilder;
import com.github.im.core.RequestCommand;
import com.github.im.protocol.AuthenticateResponse;
import com.github.im.tcp.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * @author wangsz
 * @create 2020-03-29
 **/
public class DispatcherClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * 端口连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf message = (ByteBuf) msg;
        DataPackage dataPackage = new DataPackage(message);
        if(dataPackage.getRequestType() == RequestCommand.AUTHENTICATE) {
            AuthenticateResponse response = AuthenticateResponse.parseFrom(dataPackage.getBody());
            String uid = response.getUid();
            SessionManager sessionManager = SessionManager.getInstance();
            SocketChannel session = sessionManager.getSession(uid);
            ByteBuf byteBuf = ProtocolByteBufBuilder.buildByteBuf(dataPackage.getBody(), RequestCommand.AUTHENTICATE);
            session.writeAndFlush(byteBuf);
            //认证失败移除本地session的缓存
            if(response.getStatus() != ImConstants.RESPONSE_STATUS_OK) {
                sessionManager.removeSession(session);
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
