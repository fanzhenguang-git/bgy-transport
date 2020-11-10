/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.impl;

import com.sunseaiot.deliver.dao.TransportConfigDao;
import com.sunseaiot.deliver.dao.entity.TransportConfigEntity;
import com.sunseaiot.deliver.dao.repository.TransportConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TransportConfigDao实现
 *
 * @author: xwb
 * @create: 2019/11/14 13:56
 */
@Component
public class TransportConfigDaoImpl implements TransportConfigDao {

    @Autowired
    private TransportConfigRepository transportConfigRepository;

    @Override
    public List<TransportConfigEntity> findByProductName(String productName) {
        return transportConfigRepository.findByProductName(productName);
    }

    @Override
    public TransportConfigEntity findByDeviceId(String deviceId) {
        return transportConfigRepository.findByDeviceId(deviceId);
    }

    @Override
    public TransportConfigEntity findByConfigName(String configName) {
        return transportConfigRepository.findByConfigName(configName);
    }
}
