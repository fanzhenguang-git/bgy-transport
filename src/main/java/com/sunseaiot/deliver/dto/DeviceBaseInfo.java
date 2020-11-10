/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dto;

import lombok.Data;

/**
 * @author hupan
 * @date 2019-09-06.
 */
@Data
public class DeviceBaseInfo {

    // 设备id
    private String deviceId;

    // 设备名称
    private String deviceName;

    // 厂商id
    private String manufacturerId;
}
