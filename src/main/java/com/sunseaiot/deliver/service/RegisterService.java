/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service;

/**
 * 设备注册接口
 *
 * @author hupan
 * @date 2019-08-13.
 */
public interface RegisterService {

    // 注册通知服务
    String registerNotification(NotificationService notificationService);

    // 注册设备
    void registerDevice(String serviceId, String deviceId);

    // 移除设备
    void removeDevice(String serviceId, String deviceId);
}
