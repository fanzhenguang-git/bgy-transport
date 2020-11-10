/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = EntityConstant.TABLE_DEVICE_RAW)
@EntityListeners(AuditingEntityListener.class)
public class DeviceRawEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, unique = true)
    private Long id;

    // 设备id
    @Column(name = EntityConstant.COLUMN_DEVICE_ID, nullable = false, columnDefinition = "char(36)")
    private String deviceId;

    // 设备原始数据内容
    @Column(name = EntityConstant.COLUMN_CONTENT, length = 4096)
    private String content;

    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATE_TIME, nullable = false, updatable = false)
    private Date createTime;
}
