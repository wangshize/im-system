package com.github.im.tcp;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;

/**
 * @author wangsz
 * @create 2020-03-28
 **/
public class PushManager {

    private PushThread pushThread;

    public void start() {
        new PushThread().start();
    }

    class PushThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(30 * 1000);

                    String testUserId = "test002";

                    ChannelManager channelManager = ChannelManager.getInstance();
                    SocketChannel socketChannel = channelManager.getChannel(testUserId);
                    if(socketChannel != null) {
                        String pushMsg = "test001向你推送一条消息#";
                        socketChannel.writeAndFlush(Unpooled.wrappedBuffer(pushMsg.getBytes()));
                    } else {
                        System.out.println("客户端：" + testUserId + "已下线");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
