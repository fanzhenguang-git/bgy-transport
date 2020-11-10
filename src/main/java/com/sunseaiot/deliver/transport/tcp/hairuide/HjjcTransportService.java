/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.hairuide;

import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 环境监测上行服务
 *
 * @author fanbaochun
 * @date 2019-09-24
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.tcp.enable", havingValue = "true")
public class HjjcTransportService {

    @Autowired
    private UpstreamService upstreamService;

    /**
     * @param bytes message字节组
     * @Description 上行数据处理
     */
    public void payLoadHandler(byte[] bytes) {
        if (bytes == null || bytes.length < 1) {
            log.debug("array data is null");
            return;
        }
        try {
            // 数据字节数
            byte charLength = bytes[14];
            int dataStart = 14 + 3 + 1;
            // 有效数据 字节组下标 结束位置 = dataStart + 数据字节（charLength）
            int dataEnd = dataStart + (charLength & 0xff);
            byte checkCountH = bytes[dataEnd];
            byte checkCountL = bytes[dataEnd + 1];
            // 计算校验和
            int checkCountNew = PayLoadUtil.getCheckCount1(0, dataEnd, bytes);
            // 校验和 to byte
            byte[] checkCountNewBytes = PayLoadUtil.intToByte(checkCountNew);
            // 校验
            if (checkCountH != checkCountNewBytes[0] || checkCountL != checkCountNewBytes[1]) {
                log.debug("Check failed check1 {}, {}", checkCountH, checkCountL);
                log.debug("Check failed check2 {}, {}", checkCountNewBytes[0], checkCountNewBytes[1]);
                return;
            }
            // 终端唯一标识
            String imei = PayLoadUtil.getBytesStr(3, 11, bytes);
            // 有效数据
            byte[] data = PayLoadUtil.getBytes(dataStart, dataEnd, bytes);
            Map<String, TypeValue> typeValueMap = getData(data);
            typeValueMap.put("imei", new TypeValue().setStringValue(imei));
            String jsonStr = PayLoadUtil.map2JsonStr(typeValueMap);
            log.info("jsonStr:" + jsonStr);
            // 获取设备id
            DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth("", imei, "");
            String deviceId = deviceBaseInfo == null ? "" : deviceBaseInfo.getDeviceId();
            // 上传原始数据
            upstreamService.postOriginData(deviceId, jsonStr);
            upstreamService.postDeviceData(deviceId, typeValueMap);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DeliverException(DeliverError.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, TypeValue> getData(byte[] bytes) {
        int size = bytes.length;
        // 湿度  H 高字节  L 低字节 计算公式(H*256+L)/10;
        int sdH = PayLoadUtil.compareNumberSize(0, size) ? bytes[0] & 0xff : 0;
        int sdL = PayLoadUtil.compareNumberSize(1, size) ? bytes[1] & 0xff : 0;
        Double sdValue = Double.valueOf((sdH * 256 + sdL) / 10);
        // 温度  H 高字节  L 低字节 计算公式(H*256+L)/10;
        int wdH = PayLoadUtil.compareNumberSize(2, size) ? bytes[2] & 0xff : 0;
        int wdL = PayLoadUtil.compareNumberSize(3, size) ? bytes[3] & 0xff : 0;
        Double wdValue = Double.valueOf((wdH * 256 + wdL) / 10);
        // 噪声  H 高字节  L 低字节  计算公式(H*256+L)/10;
        int zsH = PayLoadUtil.compareNumberSize(4, size) ? bytes[4] & 0xff : 0;
        int zsL = PayLoadUtil.compareNumberSize(5, size) ? bytes[5] & 0xff : 0;
        Double zsValue = Double.valueOf((zsH * 256 + zsL) / 10);
        // PM2.5  H 高字节  L 低字节 计算公式(H*256+L)/10;
        int pmH = PayLoadUtil.compareNumberSize(6, size) ? bytes[6] & 0xff : 0;
        int pmL = PayLoadUtil.compareNumberSize(7, size) ? bytes[7] & 0xff : 0;
        Double pmValue = Double.valueOf((pmH * 256 + pmL) / 10);
        // PM10  H 高字节  L 低字节 计算公式(H*256+L)/10;
        int pmTenH = PayLoadUtil.compareNumberSize(8, size) ? bytes[8] & 0xff : 0;
        int pmTenL = PayLoadUtil.compareNumberSize(9, size) ? bytes[9] & 0xff : 0;
        Double pmTenValue = Double.valueOf((pmTenH * 256 + pmTenL) / 10);
        // 风向  H 高字节  L 低字节
        int fxH = PayLoadUtil.compareNumberSize(10, size) ? bytes[10] & 0xff : 0;
        // 0 北 、 1 东北 、2 东 、3 东南 、4 南、5 西南、6 西 、7 西北
        int fxL = PayLoadUtil.compareNumberSize(11, size) ? bytes[11] & 0xff : 0;
        // 风速  H 高字节  L 低字节 计算公式(H*256+L)/10;
        int fsH = PayLoadUtil.compareNumberSize(12, size) ? bytes[12] & 0xff : 0;
        int fsL = PayLoadUtil.compareNumberSize(13, size) ? bytes[13] & 0xff : 0;
        Double fsValue = Double.valueOf((fsH * 256 + fsL) / 10);
        Map<String, TypeValue> typeValueMap = new HashMap<>();
        typeValueMap.put("humidityValue", new TypeValue().setDoubleValue(sdValue));
        typeValueMap.put("temperatureValue", new TypeValue().setDoubleValue(wdValue));
        typeValueMap.put("noiseValue", new TypeValue().setDoubleValue(zsValue));
        typeValueMap.put("pmValue", new TypeValue().setDoubleValue(pmValue));
        typeValueMap.put("pmTenValue", new TypeValue().setDoubleValue(pmTenValue));
        typeValueMap.put("windDirectionValue", new TypeValue().setIntValue(fxL));
        typeValueMap.put("windSpeedValue", new TypeValue().setDoubleValue(fsValue));
        return typeValueMap;
    }
}
