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
@Table(name = EntityConstant.TABLE_USER)
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GenericGenerator(name = "java-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "java-uuid")
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, columnDefinition = "char(36)")
    private String id;

    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_TENANT_ID, nullable = false, columnDefinition = "char(36)")
    private TenantEntity tenantEntity;

    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_ORGANIZATION_ID, columnDefinition = "char(36)")
    private OrganizationEntity organizationEntity;

    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_ROLE_ID, nullable = false, columnDefinition = "char(36)")
    private RoleEntity roleEntity;

    @Column(name = EntityConstant.COLUMN_NAME, nullable = false, length = 32)
    private String name;

    @Column(name = EntityConstant.COLUMN_PHONE_NUM, length = 16)
    private String phoneNum;

    @Column(name = EntityConstant.COLUMN_IDENTITY, nullable = false, length = 32)
    private String identity;

    @Column(name = EntityConstant.COLUMN_CREDENTIAL, nullable = false, length = 64)
    private String credential;

    @Column(name = EntityConstant.COLUMN_ENABLE, nullable = false)
    private boolean enable;

    @Column(name = EntityConstant.COLUMN_SUPER_ADMIN, nullable = false)
    private boolean superAdmin;

    @Column(name = EntityConstant.COLUMN_LOGIN_IP)
    private String loginIp;

    @Column(name = EntityConstant.COLUMN_LOGIN_ADDRESS)
    private String loginAddress;

    @Column(name = EntityConstant.COLUMN_LOGIN_AT, columnDefinition = "datetime(3)")
    private Date loginAt;

    @Column(name = EntityConstant.COLUMN_LOGIN_FIRST_TIME, nullable = false)
    private boolean loginFirstTime;

    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATED_AT, nullable = false, updatable = false, columnDefinition = "datetime(3)")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = EntityConstant.COLUMN_UPDATED_AT, columnDefinition = "datetime(3)")
    private Date updatedAt;
}
