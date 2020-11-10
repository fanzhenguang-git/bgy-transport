/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * 指令下发动作实体类 存入redis
 *
 * @author fanbaochun
 * @date 2019/9/24
 */
@Data
public class TcpDeviceCacheEntity {

    private String deviceId;

    private String imei;

    private Date reqTime;

    private String actionName;

    private JSONObject typeValueObj;
}
