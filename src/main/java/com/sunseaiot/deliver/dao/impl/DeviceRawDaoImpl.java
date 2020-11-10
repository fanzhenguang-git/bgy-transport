/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.impl;

import com.sunseaiot.deliver.dao.DeviceRawDao;
import com.sunseaiot.deliver.dao.entity.DeviceRawEntity;
import com.sunseaiot.deliver.dao.repository.DeviceRawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DeviceRawDao实现
 *
 * @author hupan
 * @date 2019-09-03.
 */
@Component
public class DeviceRawDaoImpl implements DeviceRawDao {

    @Autowired
    private DeviceRawRepository deviceRawRepository;

    @Override
    public DeviceRawEntity save(DeviceRawEntity deviceRawEntity) {
        return deviceRawRepository.save(deviceRawEntity);
    }
}
