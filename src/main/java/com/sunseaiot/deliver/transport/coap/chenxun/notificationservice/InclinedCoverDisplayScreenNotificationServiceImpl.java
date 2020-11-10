/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.chenxun.notificationservice;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.transport.coap.chenxun.InclinedCoverEncodedData;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 下行数据编码
 *
 * @author jyl
 * @date 2019-08-27
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.coap.enable", havingValue = "true")
public class InclinedCoverDisplayScreenNotificationServiceImpl implements NotificationService {

    private final RegisterService registerService;

    public InclinedCoverDisplayScreenNotificationServiceImpl(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostConstruct
    public void init() {
        String serviceId = registerService.registerNotification(this);
        registerService.registerDevice(serviceId, "2c5a70d9-ffbf-4af2-8d93-6e48f67d65fc");
    }

    @Override
    public void onOriginData(String deviceId, String payload) {
    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {
    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {

        Map<String, String> deviceMap = new HashMap<>(16);
        // todo 需要初始化设备id与设备上传数据ip地址端口，目前晨讯下行未确定，需要确认后在进行处理
        deviceMap.put("2c5a70d9-ffbf-4af2-8d93-6e48f67d65fc", "0.0.0.0:5864/time");
        String uri = deviceMap.get(deviceId);
        InclinedCoverEncodedData encodedData = new InclinedCoverEncodedData();
        String playloads = "";
        String checkTime = "checkTime";
        String nextCycle = "nextCycle";
        if (checkTime.equals(actionName)) {
            playloads = encodedData.encodDataMissingReport(parameterMap);
        }
        if (nextCycle.equals(actionName)) {
            playloads = encodedData.encodData(parameterMap);
        }
        CoapClient client = new CoapClient(uri);
        try {
            client.post(playloads, 2);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DeliverException(DeliverError.INTERNAL_SERVER_ERROR);
        }
    }
}