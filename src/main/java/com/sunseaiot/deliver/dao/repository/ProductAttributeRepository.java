/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.repository;

import com.sunseaiot.deliver.dao.entity.ProductAttributeEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 产品属性信息数据存储库
 *
 * @author hupan
 * @date 2019-07-17.
 **/
@Repository
public interface ProductAttributeRepository extends CrudRepository<ProductAttributeEntity, String>, JpaSpecificationExecutor<ProductAttributeEntity> {

    List<ProductAttributeEntity> findAllByProductId(String productId);

    Optional<ProductAttributeEntity> findByProductIdAndName(String productId, String name);
}
