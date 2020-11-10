/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao;

import com.sunseaiot.deliver.dao.entity.ProductAttributeEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 产品数据dao层接口
 *
 * @author hupan
 * @date 2019-08-15.
 */
public interface ProductAttributeDao {

    List<ProductAttributeEntity> findAllByProductId(String productId);

    Map<String, ProductAttributeEntity> findAttributeNameMapByProductId(String productId);

    Optional<ProductAttributeEntity> findByProductIdAndName(String productId, String name);
}
