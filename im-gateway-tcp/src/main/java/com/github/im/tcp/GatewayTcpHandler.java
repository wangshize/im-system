package com.github.im.tcp;

import com.alibaba.fastjson.JSON;
import com.github.im.core.RequestCommand;
import com.github.im.core.UserRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;

/**
 * @author wangsz
 * @create 2020-03-24
 **/
public class GatewayTcpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("跟客户建立连接完成：" + ctx.channel().remoteAddress().toString());
    }

    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        ChannelManager channelManager = ChannelManager.getInstance();
        channelManager.removeChannel(channel);
        System.out.println("客户端连接断开，删除其连接缓存:" + channel.remoteAddress().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //客户端的SocketChannel
        //token认证完毕，需要将这个channel缓存起来
        //如果需要对这个客户端推送消息，直接找到这个SocketChannel推送消息
        //以上就是服务端主动发送消息到客户端
        String message = (String) msg;
        System.out.println(message);
        UserRequest request = JSON.parseObject(message, UserRequest.class);
        if (RequestCommand.AUTHENTICATE.equals(request.getCommand())) {
            String userId = request.getUserId();
            String token = request.getToken();
            //获取到token到单点登录服务检查授权

            //认证成功的话，将这个连接缓存起来
            Channel channel = ctx.channel();
            ChannelManager.getInstance().addChannel(userId, (SocketChannel) channel);
            System.out.println("对用户发起认证确认完毕，缓存客户端长链接：" + ctx.channel().remoteAddress().toString());
        } else {
            String userId = request.getUserId();
            if (!ChannelManager.getInstance().exsiteChannel(userId)) {
                byte[] responseBytes = "未认证用户#".getBytes();
                ctx.writeAndFlush(Unpooled.wrappedBuffer(responseBytes));
            } else {
                System.out.println("将消息分发到kafka中:" + request.getMessage());
            }
        }
        ctx.fireChannelRead(msg);
    }


//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//
//    }
}
