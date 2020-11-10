/**
 * Copyright © 2016-2019 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunseaiot.deliver.transport.tcp.netty;

import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.boda.ManholeCoverTransportService;
import com.sunseaiot.deliver.transport.tcp.hairuide.HjjcTransportService;
import com.sunseaiot.deliver.transport.tcp.rongjia.TrashTransportUpstreamService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.tcp.enable", havingValue = "true")
public class TcpNettyServer {

    @Value("${deliver.transport.tcp.bind-host}")
    private String host;

    @Value("${deliver.transport.tcp.bind-port}")
    private Integer port;

    @Value("${deliver.transport.tcp.netty.boss-thread-count}")
    private Integer bossGroupThreadCount;

    @Value("${deliver.transport.tcp.netty.worker-thread-count}")
    private Integer workerGroupThreadCount;

    @Autowired
    private UpstreamService upstreamService;

    @Autowired
    private TrashTransportUpstreamService trashTransportUpstreamService;

    @Autowired
    private ManholeCoverTransportService manholeCoverTransportService;

    @Autowired
    private HjjcTransportService hjjcTransportService;

    private Channel serverChannel;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    @PostConstruct
    public void init() throws Exception {
        log.info("Starting TCP server...");
        bossGroup = new NioEventLoopGroup(bossGroupThreadCount);
        workerGroup = new NioEventLoopGroup(workerGroupThreadCount);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                // 保持连接数
                .option(ChannelOption.SO_BACKLOG, 128)
                // 有数据立即发送
                .option(ChannelOption.TCP_NODELAY, true)
                // 保持连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new TcpServerInitializer(upstreamService, trashTransportUpstreamService, manholeCoverTransportService, hjjcTransportService));
        serverChannel = b.bind(host, port).sync().channel();
        log.info("TCP server started!");
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        log.info("Stopping TCP server!");
        try {
            serverChannel.close().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        log.info("TCP server stopped!");
    }
}
