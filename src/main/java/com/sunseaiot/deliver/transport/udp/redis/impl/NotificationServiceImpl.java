/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.redis.impl;

import com.sunseaiot.deliver.dao.entity.TransportConfigEntity;
import com.sunseaiot.deliver.dao.repository.TransportConfigRepository;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.udp.redis.UdpOperateCacheDao;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 指令下行实现类
 *
 * @author qixiaofei
 * @date 2019-09-29.
 */
@Slf4j
@Component
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UdpOperateCacheDao udpOperateCacheDao;

    @Autowired
    private UpstreamService upstreamService;

    @Autowired
    TransportConfigRepository transportConfig;

    private final static String fireProductName = "腾联消防栓-闷盖";

    private final static String pressProductName = "腾联消防栓-水压";

    @Getter
    private String serviceId;

    @PostConstruct
    public void init() {
        log.info("register udp device,fireProductId={},pressProductId={}", fireProductName, pressProductName);
        this.serviceId = registerService.registerNotification(this);
        List<TransportConfigEntity> fireList = transportConfig.findByProductName(fireProductName);
        // 注册设备
        fireList.forEach(entity -> {
            registerService.registerDevice(serviceId, entity.getDeviceId());
        });
        List<TransportConfigEntity> pressList = transportConfig.findByProductName(pressProductName);
        pressList.forEach(entity -> {
            registerService.registerDevice(serviceId, entity.getDeviceId());
        });
    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {

    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {
        try {
            TypeValue typeValue = parameterMap.get("value");
            String value = "";
            if (actionName.equals("batteryAlarm")) {
                value = String.valueOf(typeValue.getIntValue());
            } else if (actionName.equals("defense")) {
                value = String.valueOf(typeValue.getBoolValue());
            } else if (actionName.equals("heart")) {
                value = String.valueOf(typeValue.getIntValue() * 60);
            } else if (actionName.equals("ipPort")) {
                value = parameterMap.get("address").getStringValue() + "." + parameterMap.get("port").getIntValue().toString();
            } else {
                throw new Exception("actionName error,actionName=" + actionName);
            }
            DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth(deviceId, null, null);
            String deviceName = deviceBaseInfo.getDeviceName();
            udpOperateCacheDao.save(deviceName, actionName, value);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
