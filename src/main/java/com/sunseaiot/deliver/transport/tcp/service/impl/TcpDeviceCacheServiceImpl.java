/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.transport.tcp.constant.TcpRedisConstant;
import com.sunseaiot.deliver.transport.tcp.entity.TcpDeviceCacheEntity;
import com.sunseaiot.deliver.transport.tcp.service.TcpDeviceCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author fanbaochun
 * @date 2019-09-29
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.tcp.enable", havingValue = "true")
public class TcpDeviceCacheServiceImpl implements TcpDeviceCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(TcpDeviceCacheEntity tcpDeviceCacheEntity) {
        redisTemplate.opsForHash().put(getKey(tcpDeviceCacheEntity.getImei()), tcpDeviceCacheEntity.getActionName(),
                String.valueOf((JSONObject) JSONObject.toJSON(tcpDeviceCacheEntity)));
    }

    @Override
    public void delete(String deviceId, String actionName) {
        redisTemplate.delete(getKey(deviceId));
    }

    @Override
    public TcpDeviceCacheEntity findLast(String deviceId, String actionName) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();
        String cacheStr = operations.get(getKey(deviceId), actionName);
        TcpDeviceCacheEntity tcpDeviceCacheEntity = JSONObject.parseObject(cacheStr, TcpDeviceCacheEntity.class);
        return tcpDeviceCacheEntity;
    }

    @Override
    public String getKey(String deviceId) {
        return TcpRedisConstant.TCP_DEVICE_LATEST_ACTION + deviceId;
    }
}
