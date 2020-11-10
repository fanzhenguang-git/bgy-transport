/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.repository;

import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备信息存储库
 *
 * @author hupan
 * @date 2019-07-17.
 **/
@Repository
public interface DeviceRepository extends CrudRepository<DeviceEntity, String>, JpaSpecificationExecutor<DeviceEntity> {

    List<DeviceEntity> findAll(Specification<DeviceEntity> specification);

    List<DeviceEntity> findAllByName(String name);
}
