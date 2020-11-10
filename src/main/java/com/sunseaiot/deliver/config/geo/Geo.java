/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.config.geo;

import com.sunseaiot.deliver.config.ApplicationProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 地磁
 *
 * @author hexing
 * @date 2019/12/24 16:35
 */
@Data
public class Geo implements Serializable {

    // lwm2m协议
    private Lwm2m lwm2m;

    // mqtt协议
    private Mqtt mqtt;


    public static class Mqtt extends ApplicationProperties.Protocal {

    }

    public static class Lwm2m extends ApplicationProperties.Protocal {

    }

    public static class Tup extends ApplicationProperties.Protocal {

    }

    public static class Tink extends ApplicationProperties.Protocal {

    }
}
