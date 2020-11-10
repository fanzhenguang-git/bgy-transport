/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.redis.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import com.sunseaiot.deliver.constant.RedisConstant;
import com.sunseaiot.deliver.transport.udp.redis.UdpOperateCacheDao;
import com.sunseaiot.deliver.transport.udp.redis.entity.UdpOperateEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * redis实现类
 *
 * @author qixiaofei
 * @date 2019-09-15.
 */
@Slf4j
@Component
public class UdpOperateCacheDaoImpl implements UdpOperateCacheDao {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 缓存redis的操作指令
     *
     * @param deviceId   设备上的地址值（不是ID)
     * @param actionName batteryAlarm-电池电压阈值（%），heart-定时上报时间间隔（整点，分钟），defense-布防/撤防 ，ipPort-前置机ip port
     * @param value      具体操作的值 04 1-布防，0-撤防;05 "216.147.1.66.2083";
     */
    @Override
    public void save(String deviceId, String actionName, String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("actionName", actionName);
        jsonObject.addProperty("value", value);
        jsonObject.addProperty("createTime", sdf.format(new Date()));
        try {
            redisTemplate.opsForValue().set(getKey(deviceId), jsonObject.toString());
        } catch (Exception e) {
            log.error("redis exception");
        }

    }

    /**
     * 删除redis中的值
     *
     * @param deviceId 设备上的address（不是ID)
     */
    @Override
    public void delete(String deviceId) {
        redisTemplate.delete(getKey(deviceId));
    }

    /**
     * 根据deviceId查询redis中缓存的操作指令
     *
     * @param deviceId 设备上的address（不是ID)
     * @return 指令对象
     */
    @Override
    public UdpOperateEntity findLatest(String deviceId) {
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        String myValue = value.get(getKey(deviceId));
        UdpOperateEntity udpOperateEntity = JSON.parseObject(myValue, UdpOperateEntity.class);
        return udpOperateEntity;
    }

    /**
     * 区分redis，增加前缀
     */
    private String getKey(String deviceId) {
        return RedisConstant.DEVICE_LATEST_OPERATE + deviceId;
    }
}
