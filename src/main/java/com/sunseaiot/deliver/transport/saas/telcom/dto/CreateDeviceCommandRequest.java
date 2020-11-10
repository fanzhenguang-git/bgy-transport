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
 * @date 2019/5/13.
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class CreateDeviceCommandRequest {

    private String deviceId;

    private Command command;

    private String callBackUrl;

    private Long expireTime;

    private Integer maxRetransmit;

}
