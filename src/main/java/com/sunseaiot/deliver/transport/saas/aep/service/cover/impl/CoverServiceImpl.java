/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.service.cover.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dao.DeviceRawDao;
import com.sunseaiot.deliver.dao.entity.DeviceRawEntity;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.saas.aep.service.CoverService;
import com.sunseaiot.deliver.util.TypeValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: wanglong
 * @Date: 2019/12/31
 **/
@Slf4j
@Component
public class CoverServiceImpl implements CoverService {

    private final UpstreamService upstreamService;

    public CoverServiceImpl(UpstreamService upstreamService) {
        this.upstreamService = upstreamService;
    }

    @Autowired
    DeviceRawDao deviceRawDao;

    @Override
    public String upDataPlayLoad(String str) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        // 获取产品参数
        String deviceId = jsonObject.getString("deviceId");
        if (StringUtils.isBlank(deviceId)) {
            log.debug("data is error!");
            return null;
        }
        String uuid = null;
        // 获取数据库设备uuid
        DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth(deviceId, "", "");
        if (null != deviceBaseInfo)
            uuid = deviceBaseInfo.getDeviceId();
        if (StringUtils.isBlank(uuid)) {
            log.debug("deviceId is null!");
            return null;
        }
        String payload = jsonObject.toString();
        // 保存原始数据到DeviceRaw表，方便查看数据
        DeviceRawEntity deviceRawEntity = new DeviceRawEntity();
        deviceRawEntity.setDeviceId(deviceId);
        deviceRawEntity.setContent(payload);
        deviceRawDao.save(deviceRawEntity);

        Map<String, TypeValue> attributes = new HashMap<>();

        TypeValue attributeValue = null;
        attributes.put("deviceId", TypeValueUtils.getAttributeValue(jsonObject.getString("deviceId"), false));
        attributeValue = TypeValueUtils.getDateFormatAttributeValue(Long.valueOf(jsonObject.getString("timestamp")), false);
        attributes.put("updateTime", attributeValue);
        jsonObject = JSONObject.parseObject(jsonObject.getString("payload"));
        if(null==jsonObject){
            jsonObject = JSONObject.parseObject(str);
            jsonObject = JSONObject.parseObject(jsonObject.getString("eventContent"));
            if(null==jsonObject){
                log.debug("data is error!");
                return null;
            }
        }
        Set<String> strings = jsonObject.keySet();
        // 封装数据
        for (String key : strings) {
            attributeValue = TypeValueUtils.getAttributeValue(jsonObject.getString(key), false);
            if ("lean_angle".equals(key)) {
                attributes.put("leanAngle", attributeValue);
            } else if ("cell_id".equals(key)) {
                attributes.put("cellId", attributeValue);
            } else if ("battery_value".equals(key)) {
                attributes.put("batteryValue", attributeValue);
            } else if ("battery_voltage".equals(key)) {
                attributes.put("batteryVoltage", attributeValue);
            } else if ("coordinate_system".equals(key)) {
                attributes.put("coordinateSystem", attributeValue);
            } else if ("water_level_state".equals(key)) {
                attributes.put("waterLevelState", attributeValue);
            } else if ("lean_angle_threshold".equals(key)) {
                attributes.put("leanAngleThreshold", attributeValue);
            } else if ("manhole_cover_open_state".equals(key)) {
                attributes.put("manholeCoverOpenState", attributeValue);
            } else if ("manhole_cover_position_state".equals(key)) {
                attributes.put("manholeCoverPositionState", attributeValue);
            } else {
                attributes.put(key, attributeValue);
            }
        }

        // 上传数据
        upstreamService.postDeviceData(uuid, attributes);
        return "";
    }

    @Override
    public String upInstructionsPlayLoad(String str) {
        return null;
    }

    @Override
    public String upEventPlayLoad(String str) {
        upDataPlayLoad(str);
        return null;
    }

    @Override
    public String upOnlineOrOfflinePlayLoad(String str) {
        return null;
    }

    @Override
    public String upAddOrDelPlayLoad(String str) {
        return null;
    }
}
