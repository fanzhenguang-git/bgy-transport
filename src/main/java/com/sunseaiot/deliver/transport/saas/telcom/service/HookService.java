/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service;

import com.google.gson.JsonObject;

/**
 * 电信回调服务接口
 *
 * @author wangyongjun
 * @date 2019/5/14.
 */
public interface HookService {

    /**
     * 处理设备信息变化通知
     *
     * @param appId      appId
     * @param jsonObject 电信NB平台推送的数据
     */
    void handleDeviceInfoChanged(String appId, JsonObject jsonObject);

    /**
     * 处理批量设备数据变化通知
     *
     * @param appId      appId
     * @param jsonObject 电信NB平台推送的数据
     */
    void handleDeviceDatasChanged(String appId, JsonObject jsonObject);

}
