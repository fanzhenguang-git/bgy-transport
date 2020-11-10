/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao;

import com.sunseaiot.deliver.dao.entity.DeviceEntity;

import java.util.List;
import java.util.Optional;

/**
 * 设备dao层接口
 *
 * @author hupan
 * @date 2019-08-14.
 */
public interface DeviceDao {

    Optional<DeviceEntity> findById(String deviceId);

    DeviceEntity findByConditions(String deviceId, String deviceName, String token);

    DeviceEntity save(DeviceEntity deviceEntity);

    List<DeviceEntity> findByProductId(String productId);
}
