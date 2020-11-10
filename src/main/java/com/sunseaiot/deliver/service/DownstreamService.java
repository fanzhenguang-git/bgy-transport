/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service;

import com.sunseaiot.deliver.dto.TypeValue;

import java.util.Map;

/**
 * 下行数据接口
 *
 * @author hupan
 * @date 2019-08-15.
 */
public interface DownstreamService {

    // 下行原始数据
    void sendOriginData(String deviceId, String payload);

    // 下行数据
    void sendData(String deviceId, Map<String, TypeValue> attributeMap);

    // 下行动作
    void sendAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap);
}
