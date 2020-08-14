package com.github.im.tcp.push;

import com.github.im.tcp.SessionManager;
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
                    Thread.sleep(10 * 1000);

                    String testUserId = "test002";

                    SessionManager clientManager = SessionManager.getInstance();
                    SocketChannel socketChannel = clientManager.getSession(testUserId);
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
