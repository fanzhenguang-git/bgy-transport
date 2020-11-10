/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.xinhaoda;

import com.sunseaiot.deliver.dto.TypeValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 地磁设备数据解析
 *
 * @author jyl
 * @date 2019-08-28
 **/
class GeomagnetismDecod {

    /**
     * 开关机数据
     * A7
     *
     * @param string 开关机数据
     * @return Map<String, TypeValue> 返回解析数据
     */
    Map<String, TypeValue> deviceOn(String string, String deviceId) {
        Map<String, TypeValue> map = new HashMap<>(16);
        String imei = string.substring(0, 15);
        String parkingNumber = string.substring(18, 24);
        // 设备状态
        boolean deviceState = false;
        String state = string.substring(24, 26);
        // 开机
        String on = "00";
        // 关机
        String off = "FF";
        if (on.equals(state.toUpperCase())) {
            deviceState = true;
        }
        if (off.equals(state.toUpperCase())) {
            deviceState = false;
        }
        map.put("imei", new TypeValue().setStringValue(imei));
        map.put("deviceState", new TypeValue().setBoolValue(deviceState));
        map.put("parkingNumber", new TypeValue().setStringValue(parkingNumber));
        map.put("deviceId", new TypeValue().setStringValue(deviceId));
        return map;
    }

    /**
     * 进出车辆
     * A8
     *
     * @param string 进出车辆数据
     * @return Map<String, TypeValue> 返回解析数据
     */
    Map<String, TypeValue> vehicleDepartureOrEntry(String string, String deviceId) {

        Map<String, TypeValue> map = new HashMap<>(16);
        String imei = string.substring(0, 15);
        boolean vehicleState = false;
        String state = string.substring(24, 26);
        // 车辆进入
        String entry = "52";
        String departure = "51";
        if (entry.equals(state.toUpperCase())) {
            vehicleState = true;
        }
        if (departure.equals(state.toUpperCase())) {
            vehicleState = false;
        }
        map.put("imei", new TypeValue().setStringValue(imei));
        map.put("vehicleState", new TypeValue().setBoolValue(vehicleState));
        map.put("deviceId", new TypeValue().setStringValue(deviceId));
        return map;
    }

    /**
     * 地磁设备电量低
     * A6
     *
     * @param string 地磁电量数据
     * @return Map<String, TypeValue> 返回解析数据
     */
    Map<String, TypeValue> batteryLow(String string, String deviceId) {
        Map<String, TypeValue> map = new HashMap<>(16);
        String imei = string.substring(0, 15);
        map.put("imei", new TypeValue().setStringValue(imei));
        map.put("deviceId", new TypeValue().setStringValue(deviceId));
        String str = string.substring(24, 26).toUpperCase();
        switch (str) {
            case "A0":
                map.put("battery", new TypeValue().setIntValue(0));
                break;
            case "A1":
                map.put("battery", new TypeValue().setIntValue(10));
                break;
            case "A2":
                map.put("battery", new TypeValue().setIntValue(20));
                break;
            default:
                map.put("battery", new TypeValue().setIntValue(30));
                break;
        }
        return map;
    }

    /**
     * 设备码记录
     * A5
     *
     * @param string 上传设备编码
     * @return Map<String, TypeValue> 返回解析数据
     */
    Map<String, TypeValue> deviceCode(String string, String deviceId) {

        Map<String, TypeValue> map = new HashMap<>(16);
        String imei = string.substring(0, 15);
        String imsi = string.substring(33, 48);
        String ccid = string.substring(48, 68);
        map.put("imei", new TypeValue().setStringValue(imei));
        map.put("imsi", new TypeValue().setStringValue(imsi));
        map.put("ccid", new TypeValue().setStringValue(ccid));
        map.put("deviceId", new TypeValue().setStringValue(deviceId));
        return map;
    }
}