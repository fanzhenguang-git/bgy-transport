/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.repository;

import com.sunseaiot.deliver.dao.entity.TransportConfigEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: tangyixiang
 * @date: 2019/11/13
 **/
@Repository
public interface TransportConfigRepository extends CrudRepository<TransportConfigEntity,String> {

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
