/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.xinhaoda;

import com.sunseaiot.deliver.dto.TypeValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备上传数据解析
 *
 * @author jyl
 * @date 2019-08-28
 */
class GeomagnetismDecodData {

    /**
     * 地磁开机
     * A8
     *
     * @param string 接收地磁开机上传数据
     * @return Map<String, TypeValue> 返回解析数据
     */
    Map<String, TypeValue> deviceOn(String string) {
        Map<String, TypeValue> map = new HashMap<>(16);
        String imei = string.substring(0, 15);
        String str = string.substring(33, 35);
        // 设备号
        map.put("imei", new TypeValue().setStringValue(imei));
        String deviceState = "开机";
        map.put("deviceState", new TypeValue().setStringValue(deviceState));
        String str1 = "51";
        String str2 = "52";
        if (str1.equals(str)) {
            map.put("vehicleState", new TypeValue().setStringValue("车辆离开"));
        }
        if (str2.equals(str)) {
            // 车辆进入
            map.put("vehicleState", new TypeValue().setStringValue("车辆进入"));
        }
        return map;
    }

    /**
     * 地磁开关机
     * A7
     *
     * @param string 解析关机数据
     * @return Map<String, TypeValue> 返回解析数据
     */
    Map<String, TypeValue> deviceOff(String string) {

        Map<String, TypeValue> map = new HashMap<>(16);
        String imei = string.substring(12, 25);
        String imsi = string.substring(29, 44);
        String deviceState = "关机";
        map.put("imei", new TypeValue().setStringValue(imei));
        map.put("imsi", new TypeValue().setStringValue(imsi));
        map.put("deviceState", new TypeValue().setStringValue(deviceState));
        return map;
    }

    /**
     * 地磁设备电量低
     * A6
     *
     * @param string 解析电量上报数据
     * @return Map<String, TypeValue> 返回解析数据
     */
    Map<String, TypeValue> batteryLow(String string) {
        Map<String, TypeValue> map = new HashMap<>(16);
        String imei = string.substring(0, 15);
        map.put("imei", new TypeValue().setStringValue(imei));
        String str = string.substring(23, 25).toUpperCase();
        switch (str) {
            case "A0":
                map.put("battery", new TypeValue().setStringValue("电量不足"));
                break;
            case "A1":
                map.put("battery", new TypeValue().setStringValue("剩余10%"));
                break;
            case "A2":
                map.put("battery", new TypeValue().setStringValue("剩余20%"));
                break;
            default:
                map.put("battery", new TypeValue().setStringValue("电量充足"));
                break;
        }
        return map;
    }

    /**
     * 车辆进入，离开
     * A5
     *
     * @param string 解析车辆状态上传数据
     * @return Map<String, TypeValue> 返回解析数据
     */
    Map<String, TypeValue> vehicleDepartureOrEntry(String string) {
        Map<String, TypeValue> map = new HashMap<>(16);
        String imei = string.substring(0, 15);
        map.put("imei", new TypeValue().setStringValue(imei));
        String str = string.substring(23, 25);
        String str1 = "51";
        String str2 = "52";
        if (str1.equals(str)) {
            // 车辆离开
            map.put("vehicleState", new TypeValue().setStringValue("车辆离开"));
        }
        if (str2.equals(str)) {
            // 车辆进入
            map.put("vehicleState", new TypeValue().setStringValue("车辆进入"));
        }
        return map;
    }
}