/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.dto;

import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class DeviceService {

    private String serviceId;

    private String serviceType;

    private JsonObject data;

    private String eventTime;

    private ServiceInfo serviceInfo;

}
