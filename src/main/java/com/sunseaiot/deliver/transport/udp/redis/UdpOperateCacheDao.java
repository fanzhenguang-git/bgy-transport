/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.redis;

import com.sunseaiot.deliver.transport.udp.redis.entity.UdpOperateEntity;

/**
 * redis接口
 *
 * @author qixiaofei
 * @date 2019-09-16
 */
public interface UdpOperateCacheDao {

    void save(String deviceId, String commandType, String value);

    void delete(String deviceId);

    UdpOperateEntity findLatest(String deviceId);
}
