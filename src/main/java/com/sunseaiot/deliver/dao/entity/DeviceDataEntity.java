/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import com.sunseaiot.deliver.dao.fieldenum.TransportDirection;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = EntityConstant.TABLE_DEVICE_DATA)
@EntityListeners(AuditingEntityListener.class)
public class DeviceDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityConstant.COLUMN_ID, nullable = false)
    private Long id;

    // 设备id
    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_DEVICE_ID, nullable = false, columnDefinition = "char(36)")
    private DeviceEntity device;

    // 属性id
    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_ATTRIBUTE_ID, nullable = false, columnDefinition = "char(36)")
    private ProductAttributeEntity attribute;

    // 传输方向 0:上报、1:下发
    @Enumerated
    @Column(name = EntityConstant.COLUMN_DIRECTION, nullable = false)
    private TransportDirection direction;

    // 布尔值
    @Column(name = EntityConstant.COLUMN_BOOL_VALUE)
    private Boolean boolValue;

    // 整型值
    @Column(name = EntityConstant.COLUMN_INT_VALUE)
    private Long intValue;

    // 浮点值
    @Column(name = EntityConstant.COLUMN_DOUBLE_VALUE)
    private Double doubleValue;

    // 字符串值
    @Column(name = EntityConstant.COLUMN_STRING_VALUE)
    private String stringValue;

    // 创建时间
    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATE_TIME, nullable = false)
    private Date createTime;
}
