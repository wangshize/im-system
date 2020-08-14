package com.github.im.tcp;

import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 长链接管理
 *
 * @author wangsz
 * @create 2020-03-28
 **/
public class SessionManager {

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return Singleton.instance;
    }

    private ConcurrentHashMap<String, SocketChannel> sessions =
            new ConcurrentHashMap<String, SocketChannel>();

    private ConcurrentHashMap<String, String> channelId2Uid =
            new ConcurrentHashMap<String, String>();

    public void addSession(String userId, SocketChannel channel) {
        String channelId = channel.id().asLongText();
        channelId2Uid.put(channelId, userId);
        sessions.put(userId, channel);
    }

    public SocketChannel getSession(String userId) {
        return sessions.get(userId);
    }

    public void removeSession(SocketChannel channel) {
        String channelId = channel.id().asLongText();
        String userId = channelId2Uid.get(channelId);
        channelId2Uid.remove(channelId);
        sessions.remove(userId);
    }

    public boolean existSession(String userId) {
        return sessions.containsKey(userId);
    }

    private static class Singleton {
        public static SessionManager instance = new SessionManager();
    }
}
