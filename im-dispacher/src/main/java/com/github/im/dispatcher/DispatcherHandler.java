package com.github.im.dispatcher;

import io.netty.channel.ChannelHandlerContext;
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
        ctx.read();
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
