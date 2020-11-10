/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 设备命令
 *
 * @author wangyongjun
 * @date 2019/5/23.
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class DeviceCommand {

    private String deviceId;

    private Map<String, PropertyValue> datapoints;

}
