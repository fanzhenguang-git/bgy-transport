/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.rongjia;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.entity.TcpDeviceCacheEntity;
import com.sunseaiot.deliver.transport.tcp.service.impl.TcpDeviceCacheServiceImpl;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * RS垃圾桶  下行
 *
 * @author fanbaochun
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.tcp.enable", havingValue = "true")
public class TrashTransportDownService implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UpstreamService upstreamService;

    @Autowired
    private TcpDeviceCacheServiceImpl tcpDeviceCacheService;

    @Getter
    private String serviceId;

    @Autowired
    private DeviceDao deviceDao;

    @Value("${deliver.product-id.rj-trash}")
    private String productId;

    @PostConstruct
    public void init() {

        this.serviceId = registerService.registerNotification(this);
        // 注册设备
        List<DeviceEntity> deviceEntitylist = deviceDao.findByProductId(productId);
        deviceEntitylist.stream().forEach(deviceEntity -> registerService.registerDevice(serviceId, deviceEntity.getId()));
    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {

    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {

        // 获取设备id
        DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth(deviceId, "", "");
        if (null == deviceBaseInfo) {
            log.error("Device is null");
            return;
        }
        try {
            String deviceName = deviceBaseInfo.getDeviceName();
            String downEntityObj = PayLoadUtil.map2JsonStr(parameterMap);
            TcpDeviceCacheEntity tcpDeviceCacheEntity = new TcpDeviceCacheEntity();
            tcpDeviceCacheEntity.setDeviceId(deviceId);
            tcpDeviceCacheEntity.setImei(deviceName);
            tcpDeviceCacheEntity.setActionName(actionName);
            tcpDeviceCacheEntity.setReqTime(new Date());
            tcpDeviceCacheEntity.setTypeValueObj(JSONObject.parseObject(downEntityObj));
            // 保存到redis
            tcpDeviceCacheService.save(tcpDeviceCacheEntity);
        } catch (Exception e) {
            log.error("Trash Transport exception: {}, {}", e.getMessage(), e.getStackTrace());
        }
    }
}
