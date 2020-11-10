/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.upstream;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dao.TransportConfigDao;
import com.sunseaiot.deliver.dao.entity.TransportConfigEntity;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import com.sunseaiot.deliver.util.TypeValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 奇迹上行服务
 *
 * @Auther: XJP
 * @Date: 2019-08-21 20:14
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class QjUpstreamService {

    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);

    @Autowired
    private TransportConfigDao transportConfigDao;

    @Autowired
    private UpstreamService upstreamService;

    /**
     * 智盒数据上行
     *
     * @param topic
     * @param payLoad
     */
    public void analysisMessage(String topic, String payLoad) {

        try {
            log.info("QjUpstreamService payload = {}",payLoad);
            JSONObject jsonObject = JSONObject.parseObject(payLoad);
            if (null == jsonObject) {
                log.warn("jsonObject data is null, skip: {}, {}", payLoad);
                return;
            }
            String devSn = (String) jsonObject.get("devSn");
            if (StringUtils.isNoneBlank(devSn)) {
                sendData(devSn, jsonObject);
            }
        } catch (Exception e) {
            log.error(QjUpstreamService.class + ", e: {}", e);
        }
    }

    private void sendData(String deviceId, JSONObject jsonObject) {

        Map<String, TypeValue> attributeValueMap = new HashMap<>();
        Set<String> strings = jsonObject.keySet();
        for (String key : strings) {
            TypeValue attributeValue = null;
            if ("updateTime".equals(key)) {
                String s = jsonObject.getString(key);
                String format = fmt.format(new Date(Long.valueOf(s)));
                attributeValue = TypeValueUtils.getAttributeValue(format, false);
            } else {
                attributeValue = TypeValueUtils.getAttributeValue(jsonObject.getString(key), false);
            }
            if (attributeValue != null) {
                attributeValueMap.put(key, attributeValue);
            }
        }
        String deviceSn = deviceId;
        DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth(null, deviceSn, null);
        if (deviceBaseInfo != null) {
            deviceId = deviceBaseInfo.getDeviceId();
        } else {
            // 根据序列号反查询配置表
            TransportConfigEntity byDeviceId = transportConfigDao.findByDeviceId(deviceId);
            if (byDeviceId == null) {
                log.error(QjUpstreamService.class + ", get deviceId is null, id: {}", deviceId);
                return;
            }
            // 获取参数
            JSONObject configObject = JSONObject.parseObject(byDeviceId.getConfigValue());
            // 赋值广播设备ID
            deviceId = configObject.getString("deviceId");
        }
        // 告警联动数据payload添加deviceId
        Map<String, TypeValue> tempMap = new HashMap<>();
        tempMap.putAll(attributeValueMap);
        tempMap.put("deviceId", new TypeValue().setStringValue(deviceId));
        String jsonStr = PayLoadUtil.map2JsonStr(tempMap);
        upstreamService.postOriginData(deviceId, jsonStr);
        //原始代码
        upstreamService.postDeviceData(deviceId, attributeValueMap);
        TypeValue online = attributeValueMap.get("online");
        Long onlineIntValue = online.getIntValue();
        upstreamService.postDeviceStatus(deviceId, onlineIntValue == 1 ? true : false);
    }
}