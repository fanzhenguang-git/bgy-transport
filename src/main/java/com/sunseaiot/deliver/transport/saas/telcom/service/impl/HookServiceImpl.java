/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiConstant;
import com.sunseaiot.deliver.transport.saas.telcom.dto.DeviceInfo;
import com.sunseaiot.deliver.transport.saas.telcom.dto.DeviceService;
import com.sunseaiot.deliver.transport.saas.telcom.service.CodecService;
import com.sunseaiot.deliver.transport.saas.telcom.service.HookService;
import com.sunseaiot.deliver.transport.saas.telcom.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电信回调服务实现类
 *
 * @author wangyongjun
 * @date 2019/5/14.
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class HookServiceImpl implements HookService {

    private final UpstreamService upstreamService;

    private final CodecService codecService;

    private final RegisterService registerService;

    private final String serviceId;

    public HookServiceImpl(CodecService codecService,
                           UpstreamService upstreamService,
                           RegisterService registerService,
                           TelcomNotificationImpl telcomNotification) {
        this.codecService = codecService;
        this.upstreamService = upstreamService;
        this.registerService = registerService;
        // 这册通知服务
        this.serviceId = registerService.registerNotification(telcomNotification);
    }

    @Async
    @Override
    public void handleDeviceInfoChanged(String appId, JsonObject jsonObject) {
        String deviceId = jsonObject.get("deviceId").getAsString();
        DeviceInfo deviceInfo = GsonUtil.fromJson(jsonObject.get("deviceInfo").toString(), DeviceInfo.class);
        // 目前只使用deviceInfoChanged中的设备状态变化信息，其他信息暂不做处理
        if (deviceInfo.getStatus() != null) {
            TelcomApiConstant.DeviceStatus deviceStatus =
                    TelcomApiConstant.DeviceStatus.valueOf(deviceInfo.getStatus());
            switch (deviceStatus) {
                case ONLINE:
                    upstreamService.postDeviceStatus(deviceId, true);
                    registerService.registerDevice(serviceId, deviceId);
                    break;
                case OFFLINE:
                case ABNORMAL:
                    upstreamService.postDeviceStatus(deviceId, false);
                    registerService.removeDevice(serviceId, deviceId);
                    break;
                default:
            }
        }
    }

    @Async
    @Override
    public void handleDeviceDatasChanged(String appId, JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get("services");
        if (jsonElement == null) {
            return;
        }
        JsonArray servicesJsonArray = jsonElement.getAsJsonArray();
        if (servicesJsonArray == null) {
            return;
        }
        String servicesJsonString = servicesJsonArray.toString();
        if (StringUtils.isBlank(servicesJsonString)) {
            return;
        }
        List<DeviceService> services = GsonUtil
                .getGson()
                .fromJson(servicesJsonString, new TypeToken<List<DeviceService>>() {
                }.getType());
        String deviceId = jsonObject.get("deviceId").getAsString();
        registerService.registerDevice(serviceId, deviceId);
        for (DeviceService deviceService : services) {
            Gson gson = GsonUtil.exclude(new GsonBuilder(), "serviceInfo").create();
            String data = gson.toJson(deviceService);
            String decodeResult = codecService.decode(appId, data);
            JsonArray keyValues = GsonUtil.fromJson(decodeResult).getAsJsonArray();
            //keyValues.forEach(j -> upstreamService.postDeviceData(deviceId, keyValueToDatapoints(j.getAsJsonObject())));
            keyValues.forEach(j -> upstreamService.postOriginData(deviceId, decodeResult));
        }
    }

    private Map<String, TypeValue> keyValueToDatapoints(JsonObject keyValue) {
        Map<String, TypeValue> stringPropertyValueMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> elementEntry : keyValue.entrySet()) {
            String key = elementEntry.getKey();
            JsonElement value = elementEntry.getValue();
            if (value.getAsJsonPrimitive().isBoolean()) {
                stringPropertyValueMap.put(key, new TypeValue().setBoolValue(value.getAsBoolean()));
            } else if (value.getAsJsonPrimitive().isString()) {
                stringPropertyValueMap.put(key, new TypeValue().setStringValue(value.getAsString()));
            } else {
                Number number = value.getAsNumber();
                if (number.toString().contains(".")) {
                    stringPropertyValueMap.put(key, new TypeValue().setDoubleValue(value.getAsDouble()));
                } else {
                    stringPropertyValueMap.put(key, new TypeValue().setIntValue(value.getAsLong()));
                }
            }
        }
        return stringPropertyValueMap;
    }
}
