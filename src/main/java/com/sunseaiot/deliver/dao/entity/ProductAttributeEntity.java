/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import com.sunseaiot.deliver.dao.fieldenum.TransportDirection;
import com.sunseaiot.deliver.dao.fieldenum.ValueCase;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = EntityConstant.TABLE_PRODUCT_ATTRIBUTE,
        uniqueConstraints = {@UniqueConstraint(columnNames = {EntityConstant.COLUMN_PRODUCT_ID,
                EntityConstant.COLUMN_NAME})})
@EntityListeners(AuditingEntityListener.class)
public class ProductAttributeEntity {

    @Id
    @GenericGenerator(name = "java-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "java-uuid")
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, columnDefinition = "char(36)")
    private String id;

    // 产品id
    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_PRODUCT_ID, nullable = false, columnDefinition = "char(36)")
    private ProductEntity product;

    // 属性名称(信号名称)
    @Column(name = EntityConstant.COLUMN_NAME, nullable = false, length = 64)
    private String name;

    // 显示名称(中文名称)
    @Column(name = EntityConstant.COLUMN_DISPLAY_NAME, length = 64)
    private String displayName;

    // 数据类型 0:只上报、1:只下发、2:双向
    @Enumerated
    @Column(name = EntityConstant.COLUMN_DIRECTION, nullable = false)
    private TransportDirection direction;

    // 数据类型 0:布尔型、1:整型、2:浮点型、3:字符串
    @Enumerated
    @Column(name = EntityConstant.COLUMN_DATA_TYPE, nullable = false)
    private ValueCase dataType;

    // 创建时间
    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATE_TIME, nullable = false, updatable = false)
    private Date createTime;

    // 修改时间
    @UpdateTimestamp
    @Column(name = EntityConstant.COLUMN_UPDATE_TIME, nullable = false)
    private Date updateTime;
}
