/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.chenxun;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.transport.coap.util.StringToHexUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 晨迅井盖上行数据解析
 *
 * @author jyl
 * @date 2019-08-21
 */
class InclinedCoverDecodingData {

    /**
     * 上报数据解析
     *
     * @param str 设备上行数据
     * @return Map<String, TypeValue> 返回解析后数据
     */
    Map<String, TypeValue> decodingData(String str) {

        Map<String, TypeValue> attributes = new HashMap<>(16);
        // 收到数据转换成Ascii码进行解析
        String strAscii = StringToHexUtil.convertHexToString(str);
        // 设备IMEI号
        String imei = strAscii.substring(12, 27);
        // 设备IMSI号
        String imsi = strAscii.substring(27, 42);
        // 小区编号
        String cellId = strAscii.substring(42, 49);
        // RSRQ
        String rsrq = strAscii.substring(49, 53);
        int checkRsrq = Integer.parseInt(rsrq, 16);
        // RSSI
        String rssi = strAscii.substring(53, 57);
        int checkRssi = Integer.parseInt(rssi, 16);
        // 状态
        String status = strAscii.substring(57, 64);
        // 电池状态
        String batteryState = strAscii.substring(64, 66);
        // 电压
        String voltage = strAscii.substring(66, 74);
        int v = Integer.parseInt(voltage, 16);
        double iVoltage = (((double) v / 4096) * 1.9 * 2 * 1000);
        // 时间
        String timeStamp = strAscii.substring(74, 82);
        long i = Integer.parseInt(timeStamp, 16);
        String formatTime = new Timestamp(i * 1000).toString();
        formatTime = formatTime.substring(0, 19);
        String communicationInterval = strAscii.substring(82, 90);
        String ctime = Integer.parseInt(communicationInterval, 16) / 60 + "";
        attributes.put("IMEI", new TypeValue().setStringValue(imei));
        attributes.put("IMSI", new TypeValue().setStringValue(imsi));
        attributes.put("CellID", new TypeValue().setStringValue(cellId));
        attributes.put("RSRQ", new TypeValue().setIntValue(checkRsrq));
        attributes.put("RSSI", new TypeValue().setIntValue(checkRssi));
        attributes.put("status", new TypeValue().setStringValue(status));
        attributes.put("batteryState", new TypeValue().setStringValue(batteryState));
        attributes.put("voltage", new TypeValue().setDoubleValue(iVoltage));
        attributes.put("timeStamp", new TypeValue().setStringValue(formatTime));
        attributes.put("communicationInterval", new TypeValue().setStringValue(ctime));
        return attributes;
    }

    /**
     * 设备数据漏报
     *
     * @param str 设备漏报数据
     * @return Map<String, TypeValue> 返回解析后数据
     */
    Map<String, TypeValue> decodingDataMissingReport(String str) {
        Map<String, TypeValue> attributes = new HashMap<>(16);
        String strAscii = StringToHexUtil.convertHexToString(str);
        // 设备状态
        String status = strAscii.substring(12, 19);
        // 时间
        String timeStamp = strAscii.substring(19, 27);
        long i = Integer.parseInt(timeStamp, 16);
        String formatTime = new Timestamp(i * 1000).toString();
        formatTime = formatTime.substring(0, 19);
        attributes.put("Status", new TypeValue().setStringValue(status));
        attributes.put("TimeStamp", new TypeValue().setStringValue(formatTime));
        return attributes;
    }
}