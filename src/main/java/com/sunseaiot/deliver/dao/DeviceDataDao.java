/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao;

import com.sunseaiot.deliver.dao.entity.DeviceDataEntity;

import java.util.Date;
import java.util.List;

/**
 * 设备数据记录daoc层接口
 *
 * @author hupan
 * @date 2019-08-14.
 */
public interface DeviceDataDao {

    DeviceDataEntity save(DeviceDataEntity deviceDataEntity);

    void saveAll(List<DeviceDataEntity> deviceDataEntityList);

    DeviceDataEntity findLatest(String deviceId, String attributeId);

    List<DeviceDataEntity> findAllByAttributeIdAndCreateTime(String attributeId, Date start, Date stop);
}
