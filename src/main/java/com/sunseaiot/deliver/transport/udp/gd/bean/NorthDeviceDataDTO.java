/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.bean;

import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dao.entity.ProductAttributeEntity;
import com.sunseaiot.deliver.dao.fieldenum.TransportDirection;
import lombok.Data;

import java.util.Date;

/**
 * 贵德智慧生态设备数据北向接口传输对象
 *
 * @author weishaopeng
 * @date 2020/1/15 16:15
 */
@Data
public class NorthDeviceDataDTO {

    // device_data 的主键id
    private Long id;

    // 设备id
    private DeviceEntity device;

    // 属性id
    private ProductAttributeEntity attribute;

    // 传输方向 0:上报、1:下发
    private TransportDirection direction;

    // 布尔值
    private Boolean boolValue;

    // 整型值
    private Long intValue;

    // 浮点值
    private Double doubleValue;

    // 字符串值
    private String stringValue;

    // 创建时间
    private Date createTime;

    // 网关透传数据过来的时间
    private String time;
}
