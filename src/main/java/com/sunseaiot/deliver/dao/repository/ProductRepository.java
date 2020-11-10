/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.repository;

import com.sunseaiot.deliver.dao.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 产品数据存储库
 *
 * @author hupan
 * @date 2019-07-17.
 **/
@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, String>,
        JpaSpecificationExecutor<ProductEntity> {

    @Query(value = "select p.* from product p ,tenant t where p.tenant_id = t.id and t.name = ?1", nativeQuery = true)
    List<ProductEntity> findAllByTenantName(String tenantName);
}
