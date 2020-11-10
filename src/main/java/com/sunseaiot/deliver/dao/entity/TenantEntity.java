/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: Guoguang Chen
 * @date: 2019/6/10
 **/
@Data
@Entity
@Table(name = EntityConstant.TABLE_TENANT)
@EntityListeners(AuditingEntityListener.class)
public class TenantEntity {

    @Id
    @GenericGenerator(name = "java-uuid-timeBased", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "java-uuid-timeBased")
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, columnDefinition = "char(36)")
    private String id;

    @Column(name = EntityConstant.COLUMN_NAME, nullable = false, length = 32)
    private String name;

    @Column(name = EntityConstant.COLUMN_PHONE_NUM, length = 16)
    private String phoneNum;

    @Column(name = EntityConstant.COLUMN_DESCRIPTION, length = 512)
    private String description;

    @Column(name = EntityConstant.COLUMN_ADDRESS, length = 128)
    private String address;

    @Column(name = EntityConstant.COLUMN_ENABLE, nullable = false)
    private Boolean enable;

    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATED_AT, nullable = false, updatable = false, columnDefinition = "datetime(3)")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = EntityConstant.COLUMN_UPDATED_AT, columnDefinition = "datetime(3)")
    private Date updatedAt;

    @Transient
    private UserEntity user;
}
