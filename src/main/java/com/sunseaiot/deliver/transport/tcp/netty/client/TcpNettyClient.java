/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author fanbaochun
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.tcp.enable", havingValue = "true")
public class TcpNettyClient {

    private void start(ByteBuf byteBuf, String host, int port) {

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                // 该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new TcpNettyClientInitializer());
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            log.info("Tcp Client start..");
            // 发送消息
            future.channel().writeAndFlush(byteBuf);
            // 等待连接被关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void sendMessage(ByteBuf byteBuf, String host, int port) {
        start(byteBuf, host, port);
    }
}
