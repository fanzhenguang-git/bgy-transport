/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.service;

import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.coap.chenxun.InclinedCoverCoapResource;
import com.sunseaiot.deliver.transport.coap.xinhaoda.GeomagnetismCoapResource;
import org.eclipse.californium.core.CoapServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * coap服务端启动入口
 *
 * @author jyl
 * @date 2019-09-03
 */
@Configuration
@ConditionalOnProperty(value = "deliver.transport.coap.enable", havingValue = "true")
public class CoapTransproService {

    /**
     * 获取配置端口号
     */
    @Value("${deliver.transport.coap.port}")
    private int port;

    @Autowired
    UpstreamService upstreamService;

    @PostConstruct
    public void upload() {

        CoapServer server = new CoapServer(port);
        // 欣浩达地磁上传数据服务端
        GeomagnetismCoapResource geomagnetismCoapResource = new GeomagnetismCoapResource("api.v1.coap.xhd",
                upstreamService);
        // 晨迅倾角井盖上传数据服务端
        InclinedCoverCoapResource qjjgCoapResource = new InclinedCoverCoapResource("api.v1.coap.cx", upstreamService);
        // 添加欣浩达服务
        server.add(geomagnetismCoapResource);
        // 添加晨迅服务
        server.add(qjjgCoapResource);
        server.start();
    }
}