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
public class SubscribeBusinessDataRequest {

    private String appId;

    private String notifyType;

    private String callbackUrl;

}
