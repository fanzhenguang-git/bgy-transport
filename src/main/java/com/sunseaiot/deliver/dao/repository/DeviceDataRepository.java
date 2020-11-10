/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.repository;

import com.sunseaiot.deliver.dao.entity.DeviceDataEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 设备数据存储库
 *
 * @author hupan
 * @date 2019-07-17.
 **/
@Repository
public interface DeviceDataRepository extends CrudRepository<DeviceDataEntity, Long>, JpaSpecificationExecutor<DeviceDataEntity> {

    DeviceDataEntity findTopByDeviceIdAndAttributeIdOrderByCreateTimeDesc(String deviceId, String attributeId);

    List<DeviceDataEntity> findAllByAttributeIdAndCreateTimeBetweenOrderByCreateTimeDesc(String attributeId,Date start, Date stop);
}
