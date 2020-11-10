/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.util;

import java.util.HashMap;

/**
 * 生成设备报文前两位的编号地址
 *
 * @author weishaopeng
 * @date 2019/12/19 18:53
 */
public class DeviceSerialIdUtil {

    public static HashMap<String, String> getDeviceSerialIdMap() {

        HashMap<String, String> deviceSerialIdMap = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "1", "NO2");
        }
        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "2", "SO2");
        }
        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "3", "CO");
        }
        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "4", "O3");
        }
        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "5", "PM2.5/PM10");
        }
        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "6", "WSD");
        }
        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "7", "FS");
        }
        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "8", "FX");
        }
        for (int i = 0; i < 9; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "9", "DQY");
        }
        for (int i = 0; i < 5; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "A", "DO");
        }
        for (int i = 0; i < 5; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "B", "RJY");
        }
        for (int i = 0; i < 5; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "C", "PH");
        }
        for (int i = 0; i < 5; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "D", "SW");
        }
        for (int i = 0; i < 5; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "E", "DDLTRY");
        }
        for (int i = 0; i < 5; i++) {
            deviceSerialIdMap.put(String.valueOf(i) + "F", "NH3");
        }
        deviceSerialIdMap.put("E" + String.valueOf(1), "DB");
        deviceSerialIdMap.put("E" + String.valueOf(2), "CO");
        deviceSerialIdMap.put("E" + String.valueOf(3), "CO2");
        deviceSerialIdMap.put("E" + String.valueOf(4), "NO");
        return deviceSerialIdMap;
    }
}
