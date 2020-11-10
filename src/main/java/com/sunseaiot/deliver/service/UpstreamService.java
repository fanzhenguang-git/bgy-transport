/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service;

import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;

import java.util.Map;

/**
 * 设备上行接口
 *
 * @author hupan
 * @date 2019-08-13.
 */
public interface UpstreamService {

    // 上传原始数据
    void postOriginData(String deviceId, String payload);

    // 上传设备数据
    void postDeviceData(String deviceId, Map<String, TypeValue> attributeMap);

    // 上传设备在线状态
    void postDeviceStatus(String deviceId, boolean isOnline);

    // 设备鉴权
    DeviceBaseInfo deviceAuth(String deviceId, String deviceName, String token);
}
