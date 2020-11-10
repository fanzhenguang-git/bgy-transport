/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.notificationservice;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.TransportConfigDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dao.entity.TransportConfigEntity;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.transport.saas.qiji.LampControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 奇迹灯控下行通知服务
 *
 * @author: xwb
 * @create: 2019/08/27 11:27
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class LampNotificationService implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private LampControl lampControl;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private TransportConfigDao transportConfigDao;

    @Value("${deliver.product-id.qj-lamp}")
    private String productId;


    @PostConstruct
    public void init() {

        String serviceId = registerService.registerNotification(this);
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

        // 查询配置表
        TransportConfigEntity byDeviceId = transportConfigDao.findByDeviceId(deviceId);
        if (byDeviceId == null) {
            log.info("devSn is not exist");
            return;
        }
        // 获取参数
        JSONObject configObject = JSONObject.parseObject(byDeviceId.getConfigValue());
        String hostPort = configObject.getString("hostPort");
        String appId = configObject.getString("appId");
        String appSecret = configObject.getString("appSecret");
        String devSn = configObject.getString("devSn");
        // 控制灯开关
        if (LampControl.LAMP_ON.equals(actionName)) {
            lampControl.handOnOff(devSn, true,2, hostPort, appId, appSecret);
        } else if (LampControl.LAMP_OFF.equals(actionName)) {
            lampControl.handOnOff(devSn, false,2, hostPort, appId, appSecret);
        }
    }
}
