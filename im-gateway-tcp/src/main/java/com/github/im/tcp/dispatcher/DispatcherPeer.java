package com.github.im.tcp.dispatcher;

import io.netty.channel.socket.SocketChannel;
import lombok.Data;

/**
 * @author wangsz
 * @create 2020-08-12
 **/
@Data
public class DispatcherPeer {

    private SocketChannel socketChannel;

    public DispatcherPeer(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

}
