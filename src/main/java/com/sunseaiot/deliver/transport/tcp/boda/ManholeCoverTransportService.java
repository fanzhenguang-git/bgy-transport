/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.boda;

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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 博大井盖上行服务
 *
 * @author fanbaochun
 * @date 2019-09-24
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.tcp.enable", havingValue = "true")
public class ManholeCoverTransportService {

    @Autowired
    private UpstreamService upstreamService;

    /**
     * @param bytes 上行message字节组
     * @Description 博大井盖message处理
     */
    public void payLoadHandler(byte[] bytes) {

        if (null == bytes || bytes.length < 1) {
            log.warn("Processing array data is null");
            return;
        }
        try {
            // imei
            String imei = PayLoadUtil.getBytesStr(3, 11, bytes);
            // 数据长度
            byte dataLength = bytes[14];
            // 有效数据
            int dataStart = 14 + 3 + 1;
            // 有效数据 字节组下标 结束位置 = dataStart + 数据字节（charLength）
            int dataEnd = dataStart + (dataLength & 0xff);
            // 有效数据
            byte[] data = PayLoadUtil.getBytes(dataStart, dataEnd, bytes);
            byte level = bytes[dataEnd];
            String imsi = PayLoadUtil.getBytesStr(dataEnd + 3, dataEnd + 3 + 8, bytes);
            Map<String, TypeValue> typeValueMap = getJgData(data);
            typeValueMap.put("level", new TypeValue().setIntValue(level & 0xff));
            typeValueMap.put("imsi", new TypeValue().setStringValue(imsi));
            typeValueMap.put("imei", new TypeValue().setStringValue(imei));
            // String jsonStr = PayLoadUtil.map2JsonStr(typeValueMap);
            // log.info("To Json {}", jsonStr);
            // 获取设备id
            DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth("", imei, "");
            String deviceId = deviceBaseInfo == null ? "" : deviceBaseInfo.getDeviceId();
            // 告警联动数据payload添加deviceId
            Map<String, TypeValue> tempMap = new HashMap<>();
            tempMap.putAll(typeValueMap);
            tempMap.put("deviceId", new TypeValue().setStringValue(deviceId));
            String jsonStr = PayLoadUtil.map2JsonStr(tempMap);
            log.info("To Json {}", jsonStr);
            // 上传原始数据
            upstreamService.postOriginData(deviceId, jsonStr);
            // 上传
            upstreamService.postDeviceData(deviceId, typeValueMap);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DeliverException(DeliverError.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param bytes 有效数据
     */
    private Map<String, TypeValue> getJgData(byte[] bytes) throws Exception {
        int size = bytes.length;
        // 开关标志 开
        byte open = 0x66;
        // 开关标志 关
        byte close = 0x0;
        // 井盖标志
        int bz = PayLoadUtil.compareNumberSize(0, size) ? bytes[0] & 0xff : 0;
        // 开关标志 66为开 0为关
        byte kg = PayLoadUtil.compareNumberSize(1, size) ? bytes[1] : 0x00;
        // 电压
        String voltage = PayLoadUtil.getBytesStr(2, 4, bytes);
        // 电压值 计算公式 2817/4096*5 = 3.43V
        Double voltageValue =
                new BigDecimal(Long.parseLong(voltage, 16)).divide(BigDecimal.valueOf(4096)).multiply(BigDecimal.valueOf(5)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        // 信号强度 2*13-113 = -87dbm
        int sign = PayLoadUtil.compareNumberSize(4, size) ? bytes[4] & 0xff : 0;
        Double signValue = new BigDecimal(2).multiply(BigDecimal.valueOf(sign)).subtract(BigDecimal.valueOf(113)).doubleValue();
        Map<String, TypeValue> typeValueMap = new HashMap<>();
        typeValueMap.put("coverMark", new TypeValue().setIntValue(bz));
        typeValueMap.put("switchSign", new TypeValue().setIntValue(kg == open ? 1 : 0));
        typeValueMap.put("voltageValue", new TypeValue().setDoubleValue(voltageValue));
        typeValueMap.put("signalIntensity", new TypeValue().setDoubleValue(signValue));
        return typeValueMap;
    }
}
