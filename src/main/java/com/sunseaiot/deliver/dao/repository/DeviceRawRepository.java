/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.repository;

import com.sunseaiot.deliver.dao.entity.DeviceRawEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 设备数据存储库
 *
 * @author hupan
 * @date 2019-07-17.
 **/
@Repository
public interface DeviceRawRepository extends CrudRepository<DeviceRawEntity, Long>, JpaSpecificationExecutor<DeviceRawEntity> {
}
