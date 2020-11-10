/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.impl;

import com.sunseaiot.deliver.dao.DeviceDataDao;
import com.sunseaiot.deliver.dao.entity.DeviceDataEntity;
import com.sunseaiot.deliver.dao.repository.DeviceDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * DeviceDataDao实现
 *
 * @author hupan
 * @date 2019-08-14.
 */
@Slf4j
@Component
public class DeviceDataDaoImpl implements DeviceDataDao {

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    // @Autowired
    // private RedisTemplate<String, String> redisTemplate;

    @Override
    public DeviceDataEntity save(DeviceDataEntity deviceDataEntity) {
        return deviceDataRepository.save(deviceDataEntity);
    }

    @Override
    public void saveAll(List<DeviceDataEntity> deviceDataEntityList) {
        deviceDataRepository.saveAll(deviceDataEntityList);
    }

    @Override
    public DeviceDataEntity findLatest(String deviceId, String attributeId) {
        return deviceDataRepository.findTopByDeviceIdAndAttributeIdOrderByCreateTimeDesc(deviceId, attributeId);
    }

    @Override
    public List<DeviceDataEntity> findAllByAttributeIdAndCreateTime(String attributeId, Date start, Date stop) {
        return deviceDataRepository.findAllByAttributeIdAndCreateTimeBetweenOrderByCreateTimeDesc(attributeId, start,
                stop);
    }
}
