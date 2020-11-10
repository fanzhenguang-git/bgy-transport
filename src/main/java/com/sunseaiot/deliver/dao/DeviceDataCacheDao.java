/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao;

import com.sunseaiot.deliver.dao.entity.DeviceDataEntity;
import com.sunseaiot.deliver.dao.fieldenum.ValueCase;
import com.sunseaiot.deliver.dto.TypeValue;

import java.util.Date;

/**
 * 设备数据缓存dao层
 *
 * @author hupan
 * @date 2019-08-14.
 */
public interface DeviceDataCacheDao {

    void save(String deviceId, String attributeId, TypeValue value, Date createTime);

    DeviceDataEntity findLatest(String deviceId, String attributeId, ValueCase dataType);
}
