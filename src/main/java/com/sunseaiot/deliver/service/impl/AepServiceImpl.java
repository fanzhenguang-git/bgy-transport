/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service.impl;

import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import com.sunseaiot.deliver.service.AepService;
import com.sunseaiot.deliver.util.RestUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Aep服务实现类
 *
 * @author hexing
 * @date 2019/12/25 10:31
 */
public abstract class AepServiceImpl implements AepService {

    @Value("${deliver.aep.app-secret}")
    private String appSecret;

    @Value("${deliver.aep.app-key}")
    private String appKey;

    @Override
    public AepDeviceCommandClient buildAepCommand() {
        return AepDeviceCommandClient.newClient().appKey(appKey).appSecret(appSecret).build();
    }
}
