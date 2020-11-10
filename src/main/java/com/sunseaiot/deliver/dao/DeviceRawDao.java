/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao;

import com.sunseaiot.deliver.dao.entity.DeviceRawEntity;

/**
 * 设备动作数据dao层接口
 *
 * @author hupan
 * @date 2019-08-14.
 */
public interface DeviceRawDao {

    DeviceRawEntity save(DeviceRawEntity deviceRawEntity);
}
