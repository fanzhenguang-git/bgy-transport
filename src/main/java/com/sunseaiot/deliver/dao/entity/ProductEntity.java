/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import com.sunseaiot.deliver.dao.fieldenum.ProductType;
import com.sunseaiot.deliver.dao.fieldenum.ResourceState;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = EntityConstant.TABLE_PRODUCT)
@EntityListeners(AuditingEntityListener.class)
public class ProductEntity {

    @Id
    @GenericGenerator(name = "java-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "java-uuid")
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, columnDefinition = "char(36)")
    private String id;

    // 产品名称(产品编码)
    @Column(name = EntityConstant.COLUMN_NAME, nullable = false, length = 64)
    private String name;

    // 显示名称(中文名称)
    @Column(name = EntityConstant.COLUMN_DISPLAY_NAME, length = 64)
    private String displayName;

    // 产品类型(1、网关 2、节点设备)
    @Enumerated
    @Column(name = EntityConstant.COLUMN_NODE_TYPE, nullable = false)
    private ProductType type;

    // 状态(有效性)(1、有效 2、无效)
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
