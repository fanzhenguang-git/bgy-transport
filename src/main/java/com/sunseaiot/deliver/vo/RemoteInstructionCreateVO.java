/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.vo;


import com.google.gson.JsonObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 远程指令创建实体类
 *
 * @Author: wanglong
 * @Date: 2020/1/10
 **/
@Data
public class RemoteInstructionCreateVO implements Serializable {
    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备imei
     */
    private String imei;

    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 指令内容
     */
    private JsonObject content;

    /**
     * 指令生效时间
     */
    private int ttl;

    /**
     * 操作者
     */
    private String operator;

    @Override
    public String toString() {
        return "RemoteInstructionCreateVO{" +
                "deviceId='" + deviceId + '\'' +
                ", imei='" + imei + '\'' +
                ", productId=" + productId +
                ", content=" + content +
                ", ttl=" + ttl +
                ", operator='" + operator + '\'' +
                '}';
    }
}
