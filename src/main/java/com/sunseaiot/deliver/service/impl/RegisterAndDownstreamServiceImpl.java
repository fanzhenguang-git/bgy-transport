/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service.impl;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.service.DownstreamService;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 设备下行数据实现
 *
 * @author hupan
 * @date 2019-08-14.
 */
@Slf4j
@Component
public class RegisterAndDownstreamServiceImpl implements RegisterService, DownstreamService {

    // 通知服务
    private final ConcurrentMap<String, NotificationService> notificationMap = new ConcurrentHashMap();

    @Autowired
    private RedisTemplate redisTemplate;

    private final String redisKey = "register-service:";

    @Override
    public String registerNotification(NotificationService notificationService) {
        log.debug("registerNotification");
        String serviceId = UUID.randomUUID().toString();
        notificationMap.put(serviceId, notificationService);
        return serviceId;
    }

    @Override
    public void registerDevice(String serviceId, String deviceId) {
        log.debug("registerDevice: {}, {}", serviceId, deviceId);
        // 判断serviceId是否存在
        checkNotificationService(serviceId);
        redisTemplate.opsForValue().set(redisKey + deviceId, serviceId);
        // 缓存deviceId
        //deviceMap.put(deviceId, serviceId);
    }

    @Override
    public void removeDevice(String serviceId, String deviceId) {
        log.debug("removeDevice: {}, {}", serviceId, deviceId);
        // 判断serviceId是否存在
        checkNotificationService(serviceId);
        redisTemplate.delete(redisKey + deviceId);
        // 移除deviceId
        //deviceMap.remove(deviceId, serviceId);
    }

    @Override
    public void sendOriginData(String deviceId, String payload) {

    }

    @Override
    public void sendData(String deviceId, Map<String, TypeValue> attributeMap) {

        log.debug("sendData: {}", deviceId);
        NotificationService notificationService = checkAndGetNotificationService(deviceId);
        try {
            notificationService.onMessage(deviceId, attributeMap);
        } catch (Exception e) {
            log.error("send data exception");
            throw new DeliverException(DeliverError.INTERNAL_SERVER_ERROR);
        }
    }

    // 下行动作
    @Override
    public void sendAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {

        log.debug("sendAction: {}, {}", deviceId, actionName);
        NotificationService notificationService = checkAndGetNotificationService(deviceId);
        try {
            notificationService.onAction(deviceId, actionName, parameterMap);
        } catch (Exception e) {
            log.error("send action exception: {}, {}", e.getMessage(), e.getStackTrace());
            throw new DeliverException(DeliverError.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkNotificationService(String serviceId) {
        if (!notificationMap.containsKey(serviceId)) {
            log.error("serviceId error: {}", serviceId);
            throw new DeliverException(DeliverError.NOTIFICATION_SERVICE_NOT_EXIST);
        }
    }

    private NotificationService checkAndGetNotificationService(String deviceId) {
        String serviceId = (String) redisTemplate.opsForValue().get(redisKey + deviceId);
        if (StringUtils.isEmpty(serviceId)) {
            log.error("device not register: {}", deviceId);
            throw new DeliverException(DeliverError.RESOURCE_NOT_FOUND);
        }
        NotificationService notificationService = notificationMap.get(serviceId);
        if (notificationService == null) {
            log.error("notificationservice error: {}", serviceId);
            throw new DeliverException(DeliverError.INTERNAL_SERVER_ERROR);
        }
        return notificationService;
    }
}
