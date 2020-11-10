/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.dto;

import lombok.Data;

/**
 * @author hupan
 * @date 2019-09-02.
 */
@Data
public class TelcomAppInfo {

    private String appId;

    private String appSecret;

    private String pluginName;
}
