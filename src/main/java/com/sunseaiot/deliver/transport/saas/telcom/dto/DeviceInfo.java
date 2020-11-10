/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author wangyongjun
 * @date 2019/5/14.
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class DeviceInfo {

    private String nodeId;

    private String name;

    private String description;

    private String manufacturerId;

    private String manufacturerName;

    private String mac;

    private String location;

    private String deviceType;

    private String model;

    private String swVersion;

    private String fwVersion;

    private String hwVersion;

    private String protocolType;

    private String bridgeId;

    private String status;

    private String statusDetail;

    private String mute;

    private String supportedSecurity;

    private String isSecurity;

    private String signalStrength;

    private String sigVersion;

    private String serialNumber;

    private String batteryLevel;
}
