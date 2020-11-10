/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * 存储设备对应的LoRa网关数据
 *
 * @author weishaopeng
 * @date 2019/12/24 17:13
 */
@Data
@Entity
@Table(name = EntityConstant.TABLE_DEVICE_LORA)
@EntityListeners(AuditingEntityListener.class)
public class DeviceLoRaEntity {

    @Id
    @GenericGenerator(name = "java-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "java-uuid")
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, columnDefinition = "char(36)")
    private String id;

    // 透传的设备原始报文(已经经过base64解析)
    @Column(name = EntityConstant.COLUMN_UP_DATA, length = 32)
    private String upData;

    // 设备唯一标识(报文前两位地址值)
    @Column(name = EntityConstant.COLUMN_DEVICE_SERIAL, length = 32)
    private String deviceSerial;

    // 终端eui
    @Column(name = EntityConstant.COLUMN_MOTE_EUI, length = 32)
    private String moteEui;

    //网关eui
    @Column(name = EntityConstant.COLUMN_GW_EUI, length = 32)
    private String gwEui;

    // 创建时间
    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATED_AT, nullable = false, updatable = false)
    private Date createTime;

    // 修改时间
    @UpdateTimestamp
    @Column(name = EntityConstant.COLUMN_UPDATED_AT)
    private Date updateTime;
}
