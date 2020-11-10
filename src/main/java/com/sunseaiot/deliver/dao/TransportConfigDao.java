/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao;

import com.sunseaiot.deliver.dao.entity.TransportConfigEntity;

import java.util.List;

/**
 * @author: xwb
 * @create: 2019/11/14 13:55
 */
public interface TransportConfigDao {

    /**
     * 根据产品名称查找
     * @param productName
     * @return
     */
    List<TransportConfigEntity> findByProductName(String productName);

    /**
     * 根据设备ID查找
     * @param deviceId
     * @return
     */
    TransportConfigEntity findByDeviceId(String deviceId);

    /**
     * 根据配置名称查找
     * @param configName
     * @return
     */
    TransportConfigEntity findByConfigName(String configName);
}
