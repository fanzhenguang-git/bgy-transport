/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp;

import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 上传至平台操作类
 *
 * @author qixiaofei
 * @date 2019-09-23.
 */
@Slf4j
@Component
public class UpstreamPlatform {

    @Autowired
    UpstreamService upstreamService;

    /**
     * 将json字符串上传至感应平台
     *
     * @param deviceBaseInfo    设备的基础属性
     * @param attributeValueMap 属性值的map对象
     */
    public void upstreamToPlatform(DeviceBaseInfo deviceBaseInfo, Map<String, TypeValue> attributeValueMap) {
        // 如果生成告警需要查询保存deviceId
        attributeValueMap.put("deviceId", new TypeValue().setStringValue(deviceBaseInfo.getDeviceId()));
        String jsonStr = PayLoadUtil.map2JsonStr(attributeValueMap);
        log.debug(jsonStr);
        upstreamService.postDeviceData(deviceBaseInfo.getDeviceId(), attributeValueMap);
        upstreamService.postOriginData(deviceBaseInfo.getDeviceId(), jsonStr);
    }
}
