/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.impl;

import com.sunseaiot.deliver.dao.ProductAttributeDao;
import com.sunseaiot.deliver.dao.entity.ProductAttributeEntity;
import com.sunseaiot.deliver.dao.repository.ProductAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ProductAttributeDao实现
 *
 * @author hupan
 * @date 2019-08-15.
 */
@Component
public class ProductAttributeDaoImpl implements ProductAttributeDao {

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Override
    public List<ProductAttributeEntity> findAllByProductId(String productId) {
        return productAttributeRepository.findAllByProductId(productId);
    }

    @Override
    public Map<String, ProductAttributeEntity> findAttributeNameMapByProductId(String productId) {
        Map<String, ProductAttributeEntity> nameMap = new HashMap<>();
        List<ProductAttributeEntity> entityList = productAttributeRepository.findAllByProductId(productId);
        entityList.forEach(entity -> nameMap.put(entity.getName(), entity));
        return nameMap;
    }

    @Override
    public Optional<ProductAttributeEntity> findByProductIdAndName(String productId, String name) {
        return productAttributeRepository.findByProductIdAndName(productId, name);
    }
}
