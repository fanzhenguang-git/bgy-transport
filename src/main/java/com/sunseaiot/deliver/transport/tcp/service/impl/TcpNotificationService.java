/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.service.impl;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author hupan
 * @date 2019-08-20.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.tcp.enable", havingValue = "true")
public class TcpNotificationService implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Getter
    private String serviceId;

    @PostConstruct
    public void init(){

        this.serviceId = registerService.registerNotification(this);
    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {

    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {

    }
}
