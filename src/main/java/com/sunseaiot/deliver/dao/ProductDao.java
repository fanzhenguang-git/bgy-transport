/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao;

import com.sunseaiot.deliver.dao.entity.ProductEntity;

/**
 * 产品属性dao层接口
 *
 * @author hupan
 * @date 2019-08-12.
 */
public interface ProductDao {

    ProductEntity findOneByCondition(String productName);
}
