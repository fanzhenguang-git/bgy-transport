/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.entity;

import com.sunseaiot.deliver.constant.EntityConstant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author: Guoguang Chen
 * @date: 2019/6/10
 **/
@Setter
@Getter
@Entity
@Table(name = EntityConstant.TABLE_ORGANIZATION)
@EntityListeners(AuditingEntityListener.class)
public class OrganizationEntity {

    // 利用组织机构id等于租户id，判读这是租户级用户不合适，因为这个默认逻辑这里不能自动生成uuid
    // @GenericGenerator(name = "java-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    // @GeneratedValue(generator = "java-uuid")
    @Id
    @Column(name = EntityConstant.COLUMN_ID, nullable = false, columnDefinition = "char(36)")
    private String id;

    @ManyToOne
    @JoinColumn(name = EntityConstant.COLUMN_TENANT_ID, nullable = false, columnDefinition = "char(36)")
    private TenantEntity tenantEntity;

    @Column(name = EntityConstant.COLUMN_NAME, nullable = false, length = 32)
    private String name;

    @CreationTimestamp
    @Column(name = EntityConstant.COLUMN_CREATED_AT, nullable = false, updatable = false, columnDefinition = "datetime(3)")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = EntityConstant.COLUMN_UPDATED_AT, columnDefinition = "datetime(3)")
    private Date updatedAt;

    @ManyToOne(cascade = CascadeType.DETACH)
    private OrganizationEntity parentOrganizationEntity;

    @OneToMany(mappedBy = "parentOrganizationEntity", cascade = CascadeType.REMOVE)
    private List<OrganizationEntity> organizations;
}
