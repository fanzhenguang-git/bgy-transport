/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Udp登录LoRa心跳定时服务
 *
 * @author weishaopeng
 * @date 2019/12/17 17:09
 */
@Slf4j
@Component
public class UdpScheduledService {

    @Autowired
    private UdpClientSocketService ucs;

    /**
     * 发送心跳到LoRa网关
     */
    @Scheduled(cron = "*/25 * * * * ?")
    public void udp4cityAirScheduled() {

        try {
            ucs.udp4cityAir();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 等待LoRa回复
     */
    @Scheduled(cron = "*/30 * * * * ?")
    public void receiveDataSchedule() {

        try {
            ucs.receiveData();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
