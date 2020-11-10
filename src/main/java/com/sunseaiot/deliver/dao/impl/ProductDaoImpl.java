/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.impl;

import com.sunseaiot.deliver.dao.ProductDao;
import com.sunseaiot.deliver.dao.entity.ProductEntity;
import com.sunseaiot.deliver.dao.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ProductDao实现
 *
 * @author hupan
 * @date 2019-08-12.
 */
@Component
public class ProductDaoImpl implements ProductDao {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductEntity findOneByCondition(String productName) {
        if (StringUtils.isEmpty(productName)) {
            return null;
        }
        Specification<ProductEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("name"), productName));
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
            return criteriaQuery.getRestriction();
        };
        Optional<ProductEntity> product = productRepository.findOne(specification);
        if (product != null) {
            return product.get();
        }
        return null;
    }
}
