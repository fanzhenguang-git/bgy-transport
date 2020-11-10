/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import com.sunseaiot.deliver.dao.fieldenum.OnlineStatus;
import com.sunseaiot.deliver.dao.fieldenum.ResourceState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = EntityConstant.TABLE_DEVICE)
@EntityListeners(AuditingEntityListener.class)
public class DeviceEntity {

    @Id
    @GenericGenerator(name = "java-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "java-uuid")
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, columnDefinition = "char(36)")
    private String id;

    // 产品id
    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_PRODUCT_ID, nullable = false, columnDefinition = "char(36)")
    private ProductEntity product;

    // 父级设备id(子设备对应的网关id)
    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_PARENT_DEVICE_ID, columnDefinition = "char(36)")
    private DeviceEntity parentDevice;

    // 厂商id
    @Column(name = EntityConstant.COLUMN_MANUFACTURER_ID, length = 128)
    private String manufacturerId;

    // 设备名称(设备唯一标识)
    @Column(name = "serial", length = 32)
    private String name;

    // 显示名称
    @Column(name = EntityConstant.COLUMN_DISPLAY_NAME, length = 32)
    private String displayName;

    // 在线状态(0、离线 1、在线)
    @Enumerated
    @Column(name = EntityConstant.COLUMN_ONLINE_STATUS)
    private OnlineStatus onlineStatus;

    // 设备认证token
    @Column(name = EntityConstant.COLUMN_TOKEN, length = 64)
    private String token;

    // 状态(有效性)(1、有效 2、无效)
    @Enumerated
    @Column(name = EntityConstant.COLUMN_STATE, nullable = false)
    private ResourceState state;

    // 创建时间
    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private Date createTime;

    // 修改时间
    @UpdateTimestamp
    @Column(name = EntityConstant.COLUMN_UPDATED_AT, nullable = false)
    private Date updateTime;
}