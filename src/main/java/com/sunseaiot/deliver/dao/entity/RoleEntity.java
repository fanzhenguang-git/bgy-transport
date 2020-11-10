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
 * @date: 2019/6/11
 **/
@Data
@Entity
@Table(name = EntityConstant.TABLE_ROLE)
@EntityListeners(AuditingEntityListener.class)
public class RoleEntity {

    @Id
    @GenericGenerator(name = "java-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "java-uuid")
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, columnDefinition = "char(36)")
    private String id;

    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_TENANT_ID, nullable = false)
    private TenantEntity tenantEntity;

    @Column(name = EntityConstant.COLUMN_NAME, nullable = false, length = 32)
    private String name;

    @Column(name = EntityConstant.COLUMN_SYSTEM_ROLE, nullable = false)
    private Boolean systemRole;

    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATED_AT, nullable = false, updatable = false, columnDefinition = "datetime(3)")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = EntityConstant.COLUMN_UPDATED_AT, columnDefinition = "datetime(3)")
    private Date updatedAt;

    /*@Column(name=EntityConstant.COLUMN_ONLY_TENANT, nullable = false)
    private Boolean onlyTenant;*/
}
