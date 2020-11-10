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
public class CreateDeviceCommandResponse {

    private String commandId;

    private String appId;

    private String deviceId;

    private Command command;

    private String callbackUrl;

    private Integer expireTime;

    private String status;

    private String creationTime;

    private String executeTime;

    private String platformIssuedTime;

    private String deliveredTime;

    private Integer issuedTimes;

    private Integer maxRetransmit;
}
