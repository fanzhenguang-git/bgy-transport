/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.transport.saas.telcom.dto.Command;
import com.sunseaiot.deliver.transport.saas.telcom.dto.CreateDeviceCommandRequest;
import com.sunseaiot.deliver.transport.saas.telcom.service.CodecService;
import com.sunseaiot.deliver.transport.saas.telcom.service.TelcomAdapterService;
import com.sunseaiot.deliver.transport.saas.telcom.service.TelcomApiService;
import com.sunseaiot.deliver.transport.saas.telcom.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 对接电信NB的adapter向上暴露的接口的实现类
 *
 * @author wangyongjun
 * @date 2019/5/23.
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class TelcomAdapterServiceImpl implements TelcomAdapterService {

    private final TelcomApiService telcomApiService;

    private final CodecService codecService;

    public TelcomAdapterServiceImpl(TelcomApiService telcomApiService, CodecService codecService) {
        this.telcomApiService = telcomApiService;
        this.codecService = codecService;
    }

    @Override
    public void postDataToDevice(String appId, String deviceId, Map<String, TypeValue> valueMap, long expireTime) {
        try {
            // 调用编解码插件接口编码下行数据
            String encodeResult = codecService.encode(appId, getJsonKeyValueFromDeviceCommand(valueMap));
            List<Command> commandList = GsonUtil.getGson()
                    .fromJson(encodeResult, new TypeToken<List<Command>>() {
                    }.getType());
            for (Command c : commandList) {
                // 调用telcom api下发设备命令
                CreateDeviceCommandRequest createDeviceCommandRequest = new CreateDeviceCommandRequest()
                        .setExpireTime(expireTime)
                        .setDeviceId(deviceId)
                        .setCommand(c);
                telcomApiService.createDeviceCommand(appId, createDeviceCommandRequest);
            }
        } catch (Exception e) {
            log.error("Post data to device,error occurs", e);
        }
    }

    private String getJsonKeyValueFromDeviceCommand(Map<String, TypeValue> valueMap) {
        Map<String, TypeValue> map = valueMap;
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, TypeValue> entry : map.entrySet()) {
            switch (entry.getValue().getValueCase()) {
                case INTVALUE:
                    jsonObject.addProperty(entry.getKey(), entry.getValue().getIntValue());
                    break;
                case BOOLVALUE:
                    jsonObject.addProperty(entry.getKey(), entry.getValue().getBoolValue());
                    break;
                case DOUBLEVALUE:
                    jsonObject.addProperty(entry.getKey(), entry.getValue().getDoubleValue());
                    break;
                case STRINGVALUE:
                    jsonObject.addProperty(entry.getKey(), entry.getValue().getStringValue());
                    break;
                default:
            }
        }
        return jsonObject.toString();
    }
}
