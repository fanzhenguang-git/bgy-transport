/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.impl;

import com.google.gson.JsonObject;
import com.sunseaiot.deliver.constant.RedisConstant;
import com.sunseaiot.deliver.dao.DeviceDataCacheDao;
import com.sunseaiot.deliver.dao.DeviceDataDao;
import com.sunseaiot.deliver.dao.entity.DeviceDataEntity;
import com.sunseaiot.deliver.dao.fieldenum.ValueCase;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * DeviceDataCacheDao实现
 *
 * @author hupan
 * @date 2019-09-04.
 */
@Slf4j
@Component
public class DeviceDataCacheDaoImpl implements DeviceDataCacheDao {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DeviceDataDao deviceDataDao;

    @Override
    public void save(String deviceId, String attributeId, TypeValue value, Date createTime) {
        JsonObject jsonObject = new JsonObject();
        switch (value.getValueCase()) {
            case BOOLVALUE:
                jsonObject.addProperty("value", value.getBoolValue());
                break;
            case INTVALUE:
                jsonObject.addProperty("value", value.getIntValue());
                break;
            case DOUBLEVALUE:
                jsonObject.addProperty("value", value.getDoubleValue());
                break;
            case STRINGVALUE:
                jsonObject.addProperty("value", value.getStringValue());
                break;
            case OBJECTVALUE:
                jsonObject.addProperty("value", value.getObjectValue());
                break;
            default:
                return;
        }
        jsonObject.addProperty("createTime", createTime.getTime());
        try {
            redisTemplate.opsForHash().put(getKey(deviceId), attributeId, jsonObject.toString());
        } catch (Exception e) {
            log.error("redis exception");
        }
    }

    @Override
    public DeviceDataEntity findLatest(String deviceId, String attributeId, ValueCase dataType) {
        DeviceDataEntity deviceDataEntity = new DeviceDataEntity();
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        String jsonString = operations.get(getKey(deviceId), attributeId);
        // operations.multiGet()
        if (jsonString == null) {
            // 从数据库中获取，并缓存到redis
            return getAndCacheDeviceData(deviceId, attributeId);
        }
        try {
            JsonObject jsonObject = GsonUtil.fromJson(jsonString).getAsJsonObject();
            deviceDataEntity.setCreateTime(new Date(jsonObject.get("createTime").getAsLong()));
            switch (dataType) {
                case BOOLVALUE:
                    deviceDataEntity.setBoolValue(jsonObject.get("value").getAsBoolean());
                    break;
                case INTVALUE:
                    deviceDataEntity.setIntValue(jsonObject.get("value").getAsLong());
                    break;
                case DOUBLEVALUE:
                    deviceDataEntity.setDoubleValue(jsonObject.get("value").getAsDouble());
                    break;
                case STRINGVALUE:
                case OBJECTVALUE:
                    deviceDataEntity.setStringValue(jsonObject.get("value").getAsString());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("json exception");
            return null;
        }
        return deviceDataEntity;
    }

    private DeviceDataEntity getAndCacheDeviceData(String deviceId, String attributeId) {

        DeviceDataEntity deviceDataEntity = deviceDataDao.findLatest(deviceId, attributeId);
        if (deviceDataEntity == null) {
            return null;
        }

        // 缓存到redis
        JsonObject jsonObject = new JsonObject();
        if (deviceDataEntity.getBoolValue() != null) {
            jsonObject.addProperty("value", deviceDataEntity.getBoolValue());
        } else if (deviceDataEntity.getIntValue() != null) {
            jsonObject.addProperty("value", deviceDataEntity.getIntValue());
        } else if (deviceDataEntity.getDoubleValue() != null) {
            jsonObject.addProperty("value", deviceDataEntity.getDoubleValue());
        } else if (deviceDataEntity.getStringValue() != null) {
            jsonObject.addProperty("value", deviceDataEntity.getStringValue());
        } else {
            log.debug("unknow");
        }
        jsonObject.addProperty("createTime", deviceDataEntity.getCreateTime().getTime());

        try {
            redisTemplate.opsForHash().put(getKey(deviceId), attributeId, jsonObject.toString());
        } catch (Exception e) {
            log.error("redis exception");
        }
        return deviceDataEntity;
    }

    private String getKey(String deviceId) {
        return RedisConstant.DEVICE_LATEST_DATA + deviceId;
    }
}
