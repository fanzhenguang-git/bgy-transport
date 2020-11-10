/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.service;

import com.sunseaiot.deliver.transport.udp.gd.bean.DeviceDataDTO;

import java.util.List;

/**
 * 处理生态设备业务逻辑
 *
 * @author weishaopeng
 * @date 2019/12/19 16:54
 */
public interface EcologyDeviceDataService {

    /**
     * 保存生态设备数据
     *
     * @param deviceDataDTOList 设备数据列表
     */
    void saveEcologyDeviceData(List<DeviceDataDTO> deviceDataDTOList);
}
