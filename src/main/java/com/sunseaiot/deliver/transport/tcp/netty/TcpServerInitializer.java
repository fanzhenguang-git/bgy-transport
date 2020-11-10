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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final UpstreamService upstreamService;

    private final TrashTransportUpstreamService trashTransportUpstreamService;

    private final ManholeCoverTransportService manholeCoverTransportService;

    private final HjjcTransportService hjjcTransportService;

    public TcpServerInitializer(UpstreamService upstreamService,
                                TrashTransportUpstreamService trashTransportUpstreamService,
                                ManholeCoverTransportService manholeCoverTransportService,
                                HjjcTransportService hjjcTransportService) {
        this.upstreamService = upstreamService;
        this.trashTransportUpstreamService = trashTransportUpstreamService;
        this.manholeCoverTransportService = manholeCoverTransportService;
        this.hjjcTransportService = hjjcTransportService;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        TcpTransportHandler handler = new TcpTransportHandler(upstreamService, trashTransportUpstreamService,
                manholeCoverTransportService, hjjcTransportService);
        pipeline.addLast(handler);
    }
}
