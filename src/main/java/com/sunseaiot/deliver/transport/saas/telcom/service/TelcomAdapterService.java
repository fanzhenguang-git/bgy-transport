/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service;

import com.sunseaiot.deliver.dto.TypeValue;

import java.util.Map;

/**
 * 对接电信NB的adapter向上暴露的接口
 *
 * @author wangyongjun
 * @date 2019/5/23.
 */
public interface TelcomAdapterService {

    /**
     * 下发命令给设备
     *
     * @param appId      appId
     * @param expireTime 命令过期时间
     */
    void postDataToDevice(String appId, String deviceId, Map<String, TypeValue> valueMap, long expireTime);
}
