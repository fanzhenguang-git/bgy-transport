/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.service;

import com.sunseaiot.deliver.transport.tcp.entity.TcpDeviceCacheEntity;

/**
 * tcp设备动作下发 redis操作
 *
 * @author fanbaochun
 * @date 2019-09-29
 */
public interface TcpDeviceCacheService {

    /**
     * 保存动作到redis
     *
     * @param tcpDeviceCacheEntity 缓存对象
     */
    void save(TcpDeviceCacheEntity tcpDeviceCacheEntity);

    /**
     * 删除动作
     *
     * @param deviceId   设备id
     * @param actionName 动作名称
     */
    void delete(String deviceId, String actionName);

    /**
     * 根据key查找最新下发动作
     *
     * @param deviceId   设备id
     * @param actionName 动作
     * @return TcpDeviceCacheEntity
     */
    TcpDeviceCacheEntity findLast(String deviceId, String actionName);

    /**
     * 获取redis key
     *
     * @param diviceId 设备id
     * @return key
     */
    String getKey(String diviceId);
}
