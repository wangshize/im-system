package com.github.im.tcp;

import com.github.im.core.DataPackage;
import com.github.im.core.RequestCommand;
import com.github.im.protocol.AuthenticateRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
        SessionManager clientManager = SessionManager.getInstance();
        clientManager.removeSession(channel);
        System.out.println("客户端连接断开，删除其连接缓存:" + channel.remoteAddress().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //客户端的SocketChannel
        //token认证完毕，需要将这个channel缓存起来
        //如果需要对这个客户端推送消息，直接找到这个SocketChannel推送消息
        //以上就是服务端主动发送消息到客户端
        ByteBuf message = (ByteBuf) msg;
        DataPackage request = new DataPackage(message);
        if (RequestCommand.AUTHENTICATE == request.getRequestType()) {
            AuthenticateRequest authenticateRequest = AuthenticateRequest.parseFrom(request.getBody());
            //获取到token到单点登录服务检查授权
            RequestHandler requestHandler = RequestHandler.getInstance();
            requestHandler.authenticate(authenticateRequest);
            //连接缓存
            Channel channel = ctx.channel();
            SessionManager sessionManager = SessionManager.getInstance();
            //维护本地会话lianjie
            String uid = authenticateRequest.getUid();
            sessionManager.addSession(uid, (SocketChannel) channel);
            //有条件可以维护到redis里也一份
            System.out.println("对用户发起认证确认完毕，缓存客户端长链接：" + ctx.channel().remoteAddress().toString());
        } else {
//            String userId = request.getUserId();
//            if (!ClientManager.getInstance().existClient(userId)) {
//                byte[] responseBytes = "未认证用户#".getBytes();
//                ctx.writeAndFlush(Unpooled.wrappedBuffer(responseBytes));
//            } else {
//                System.out.println("将消息分发到kafka中:" + request.getMessage());
//            }
        }
        ctx.fireChannelRead(msg);
    }


//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//
//    }
}
