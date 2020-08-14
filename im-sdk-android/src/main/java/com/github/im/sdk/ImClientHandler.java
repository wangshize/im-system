package com.github.im.sdk;

import com.github.im.core.DataPackage;
import com.github.im.core.RequestCommand;
import com.github.im.protocol.AuthenticateResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wangsz
 * @create 2020-03-25
 **/
public class ImClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收服务端的响应
        DataPackage response = new DataPackage(((ByteBuf) msg));
        if(response.getRequestType() == RequestCommand.AUTHENTICATE) {
            AuthenticateResponse authenticateResponse = AuthenticateResponse.parseFrom(response.getBody());
            System.out.println(String.format("认证结果 = %s", authenticateResponse.getStatus()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
