/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 设备上报数据变化VO
 *
 * @author hexing
 * @date 2019/12/26 13:44
 */
@Data
public class PostDataChangeVo implements Serializable {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 数据格式
     */
    private Integer payloadFormat;

    /**
     * 服务id
     */
    private Integer serviceId;

    /**
     * 服务类型
     */
    private Integer serviceType;

    /**
     * 数据内容
     */
    private Map<String, Object> payload;

    /**
     * 消息组
     */
    private String topic;

    /**
     * 是否二进制
     */
    private Boolean binary;

    /**
     * 设备IMEI编号
     */
    private String IMEI;

    /**
     * 设消息类型
     */
    private String messageType;

    /**
     * 设备IMSI编号
     */
    private String IMSI;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 协议
     */
    private String protocol;
}
