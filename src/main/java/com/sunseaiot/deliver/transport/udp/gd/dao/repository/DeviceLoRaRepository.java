/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.dao.repository;

import com.sunseaiot.deliver.transport.udp.gd.dao.entity.DeviceLoRaEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备网关Repository
 *
 * @author weishaopeng
 * @date 2019/12/24 17:30
 */
@Repository
public interface DeviceLoRaRepository extends CrudRepository<DeviceLoRaEntity, String>, JpaSpecificationExecutor<DeviceLoRaEntity> {

    /**
     * 根据设备地址和网关eui查看对应的设备网关数据
     *
     * @param deviceSerial 设备地址
     * @param gwEui        网关eui
     * @return 设备网关关联数据
     */
    List<DeviceLoRaEntity> findAllByDeviceSerialAndGwEui(String deviceSerial, String gwEui);
}
