/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.netty;

import com.sunseaiot.deliver.transport.tcp.entity.NettyChannelEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fanbaochun
 */
public class NettyConfig {
    
    /**
     * 存储每一个客户端接入进来时的channel对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 存储客户端接入进来的channel对象 map形式
     */
    private static Map<String, NettyChannelEntity> map = new ConcurrentHashMap();

    public static void addChannel(String id, ChannelHandlerContext ctx) {
        NettyChannelEntity nettyChannelEntity = new NettyChannelEntity();
        nettyChannelEntity.setChannelHandlerContext(ctx);
        nettyChannelEntity.setReqTime(new Date());
        map.put(id, nettyChannelEntity);
    }

    public static Map<String, NettyChannelEntity> getChannels() {
        return map;
    }

    public static NettyChannelEntity getChannel(String id) {
        return map.get(id);
    }

    public static void removeChannel(String id) {
        map.remove(id);
    }
}
