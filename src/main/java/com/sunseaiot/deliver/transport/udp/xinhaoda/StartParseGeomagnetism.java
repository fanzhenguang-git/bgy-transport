/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.xinhaoda;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 上传地磁设备数据
 *
 * @author jyl
 * @date 2019-10-17
 **/
@Slf4j
@Component
public class StartParseGeomagnetism {

    @Autowired
    UpstreamService upstreamService;

    private GeomagnetismDecod decodData = new GeomagnetismDecod();

    public String parseMessage(String str) {

        //判断设备动作类型
        String action = str.substring(16, 18);
        //deviceName与uuid对应，获取设备id
        String deviceName = str.substring(0, 15);
        String uuid;
        String playLod;
        String heart = "A4";
        Map<String, TypeValue> map = new HashMap<>(16);
        map.put("uuid", new TypeValue().setStringValue(UUID.randomUUID().toString() + System.currentTimeMillis()));
        switch (action.toUpperCase()) {
            case "A5":
                uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                map = decodData.deviceCode(str, uuid);
                upstreamService.postDeviceData(uuid, map);
                playLod = PayLoadUtil.map2JsonStr(map);
                upstreamService.postOriginData(uuid, playLod);
                break;
            case "A6":
                uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                map = decodData.batteryLow(str, uuid);
                upstreamService.postDeviceData(uuid, map);
                playLod = PayLoadUtil.map2JsonStr(map);
                upstreamService.postOriginData(uuid, playLod);
                break;
            case "A7":
                uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                map = decodData.deviceOn(str, uuid);
                upstreamService.postDeviceData(uuid, map);
                playLod = PayLoadUtil.map2JsonStr(map);
                upstreamService.postOriginData(uuid, playLod);
                break;
            case "A8":
                uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                map = decodData.vehicleDepartureOrEntry(str, uuid);
                upstreamService.postDeviceData(uuid, map);
                playLod = PayLoadUtil.map2JsonStr(map);
                upstreamService.postOriginData(uuid, playLod);
                break;
            default:
                if (heart.equals(action.toUpperCase())) {
                    uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                    map.put("heartBeat", new TypeValue().setStringValue("1"));
                    map.put("imei", new TypeValue().setStringValue(deviceName));
                    map.put("deviceId", new TypeValue().setStringValue(uuid));
                    upstreamService.postDeviceData(uuid, map);
                    playLod = PayLoadUtil.map2JsonStr(map);
                    upstreamService.postOriginData(uuid, playLod);
                }
                break;
        }
        return str;
    }
}