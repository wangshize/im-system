package com.github.im.tcp;

import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 长链接管理
 *
 * @author wangsz
 * @create 2020-03-28
 **/
public class ChannelManager {

    private ChannelManager() {
    }

    public static ChannelManager getInstance() {
        return Singleton.instance;
    }

    private ConcurrentHashMap<String, SocketChannel> channels =
            new ConcurrentHashMap<String, SocketChannel>();

    private ConcurrentHashMap<String, String> channelIds =
            new ConcurrentHashMap<String, String>();

    public void addChannel(String userId, SocketChannel channel) {
        channelIds.put(channel.remoteAddress().getHostName(), userId);
        channels.put(userId, channel);
    }

    public SocketChannel getChannel(String userId) {
        return channels.get(userId);
    }

    public void removeChannel(SocketChannel channel) {
        String hostName = channel.remoteAddress().getHostName();
        String userId = channelIds.get(hostName);
        channelIds.remove(userId);
        channels.remove(userId);
    }

    public boolean exsiteChannel(String userId) {
        return channels.containsKey(userId);
    }

    private static class Singleton {
        public static ChannelManager instance = new ChannelManager();
    }
}
