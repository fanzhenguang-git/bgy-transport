/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.redis.entity;

import lombok.Data;

import java.util.Date;

/**
 * redis指令实体类
 *
 * @author qixiaofei
 * @date 2019-09-15.
 */
@Data
public class UdpOperateEntity {

    String deviceId;

    String actionName;

    String value;

    Date createTime;
}
