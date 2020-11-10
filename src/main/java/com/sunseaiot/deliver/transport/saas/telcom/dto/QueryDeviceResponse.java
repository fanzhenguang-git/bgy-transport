/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class QueryDeviceResponse {

    private String deviceId;

    private String gatewayId;

    private String nodeType;

    private String createTime;

    private String lastModifiedTime;

    private DeviceInfo deviceInfo;

    private List<DeviceService> services;

}
