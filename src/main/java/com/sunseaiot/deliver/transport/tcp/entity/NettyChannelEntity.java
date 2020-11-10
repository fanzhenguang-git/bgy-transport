/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.entity;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.Date;

/**
 * netty channel通道实体类
 *
 * @author fanbaochun
 * @date 2019/9/24
 */
@Data
public class NettyChannelEntity {

    /**
     * channel
     */
    private ChannelHandlerContext channelHandlerContext;

    /**
     * 请求时间
     */
    private Date reqTime;
}
