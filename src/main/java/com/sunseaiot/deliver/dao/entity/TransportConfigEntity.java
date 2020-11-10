/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import lombok.Data;

import javax.persistence.*;

/**
 * Transport 配置类
 *
 * @author: tangyixiang
 * @date: 2019/11/13
 **/
@Data
@Entity
@Table(name = EntityConstant.TABLE_TRANSPORT_CONFIG)
public class TransportConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = EntityConstant.COLUMN_PRODUCT_NAME)
    private String productName;

    @Column(name = EntityConstant.COLUMN_DEVICE_ID)
    private String deviceId;

    @Column(name = EntityConstant.COLUMN_CONFIG_NAME)
    private String configName;

    @Column(name = EntityConstant.COLUMN_CONFIG_VALUE)
    private String configValue;
}
