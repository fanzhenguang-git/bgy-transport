/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.controller;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.transport.udp.gd.service.UdpClientSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * http请求模拟生态设备数据
 *
 * @author weishaopeng
 * @date 2020/1/2 9:43
 */
@Slf4j
@Controller
@RequestMapping("/ecology")
public class EcologyDeviceDataWebSocketController {

    @Autowired
    private UdpClientSocketService udpClientSocketService;

    @PostMapping("/mock")
    private void testDeviceData(@RequestBody String reply) {
        try {
            log.info("模拟生态设备数据开始:");
            JSONObject reply1 = JSONObject.parseObject(reply).getJSONObject("reply");
            if (null != reply1) {
                udpClientSocketService.parseLoraDeviceData(reply1.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
