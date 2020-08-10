package com.github.im.dispatcher;

import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 接入系统网络连接管理
 * @author wangsz
 * @create 2020-03-29
 **/
public class GatewayManager {

    public static GatewayManager getInstance() {
        return Singleton.instance;
    }

    /**
     * 接入系统实例列表
     */
    private ConcurrentHashMap<String, SocketChannel> gateWayInstances =
            new ConcurrentHashMap<String, SocketChannel>();

    /**
     * @param channelId
     * @param channel
     */
    public void addGatewayInstance(String channelId, SocketChannel channel) {
        gateWayInstances.put(channelId, channel);
    }

    public void removeGatewayInstance(String channelId) {
        gateWayInstances.remove(channelId);
    }

    private static class Singleton {
        public static GatewayManager instance = new GatewayManager();
    }
}
