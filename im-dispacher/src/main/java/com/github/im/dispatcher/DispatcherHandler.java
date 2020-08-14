package com.github.im.dispatcher;

import com.github.im.core.DataPackage;
import com.github.im.core.ImConstants;
import com.github.im.core.ProtocolByteBufBuilder;
import com.github.im.core.RequestCommand;
import com.github.im.protocol.AuthenticateRequest;
import com.github.im.protocol.AuthenticateResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;


/**
 * @author wangsz
 * @create 2020-03-29
 **/
public class DispatcherHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接入系统和分发系统建立连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        GatewayManager gatewayManager = GatewayManager.getInstance();
        SocketChannel channel = (SocketChannel)ctx.channel();
        gatewayManager.addGatewayInstance(channel.id().asLongText(), channel);
    }

    /**
     * 断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        GatewayManager gatewayManager = GatewayManager.getInstance();
        SocketChannel channel = (SocketChannel)ctx.channel();
        gatewayManager.removeGatewayInstance(channel.id().asLongText());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DataPackage dataPackage = new DataPackage((ByteBuf) msg);
        int requestType = dataPackage.getRequestType();
        if(requestType == RequestCommand.AUTHENTICATE) {
            //sso单点登陆验证token
            AuthenticateRequest request = AuthenticateRequest.parseFrom(dataPackage.getBody());
            RequestHandler requestHandler = RequestHandler.getInstance();
            AuthenticateResponse response = requestHandler.authenticate(request);
            if(response.getStatus() == ImConstants.RESPONSE_STATUS_OK) {
                SocketChannel socketChannel = (SocketChannel) ctx.channel();
                ChannelId id = socketChannel.id();
                // session写入redis
                // key:uid  value:session信息（是否认证、认证时间、接入系统channelId）
                // 这里需要写入channelId是因为后面发送消息，kafka推送消息回分发系统后，
                // 分发系统需要知道将这个消息推送会哪个接入系统
                System.out.println("认证成功，将session写入Redis");
            }
            ByteBuf byteBuf = ProtocolByteBufBuilder.buildByteBuf(response.toByteArray(), RequestCommand.AUTHENTICATE);
            ctx.writeAndFlush(byteBuf);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
