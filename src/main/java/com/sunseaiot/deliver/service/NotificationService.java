/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service;

import com.sunseaiot.deliver.dto.TypeValue;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 下行透传数据接口
 *
 * @author hupan
 * @date 2019-08-13.
 */
public interface NotificationService {

    // 下发原始数据
    void onOriginData(String deviceId, String payload);

    // 不使用，用onAction下行
    void onMessage(String deviceId, Map<String, TypeValue> attributeMap);

    // 下行数据，参数map可以为空
    void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) throws Exception;
}
