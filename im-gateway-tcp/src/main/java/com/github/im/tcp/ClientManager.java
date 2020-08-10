package com.github.im.tcp;

import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 长链接管理
 *
 * @author wangsz
 * @create 2020-03-28
 **/
public class ClientManager {

    private ClientManager() {
    }

    public static ClientManager getInstance() {
        return Singleton.instance;
    }

    private ConcurrentHashMap<String, SocketChannel> clients =
            new ConcurrentHashMap<String, SocketChannel>();

    private ConcurrentHashMap<String, String> channelId2Uid =
            new ConcurrentHashMap<String, String>();

    public void addClient(String userId, SocketChannel channel) {
        String channelId = channel.id().asLongText();
        channelId2Uid.put(channelId, userId);
        clients.put(userId, channel);
    }

    public SocketChannel getClient(String userId) {
        return clients.get(userId);
    }

    public void removeClient(SocketChannel channel) {
        String channelId = channel.id().asLongText();
        String userId = channelId2Uid.get(channelId);
        channelId2Uid.remove(channelId);
        clients.remove(userId);
    }

    public boolean existClient(String userId) {
        return clients.containsKey(userId);
    }

    private static class Singleton {
        public static ClientManager instance = new ClientManager();
    }
}
