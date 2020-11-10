/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

/**
 * mapToBean工具
 *
 * @author hexing
 * @date 2019/12/26 09:33
 */
@Slf4j
public class BeanUtil {

    /**
     * mapToBean
     *
     * @Author: wanglong
     * @Date: 2020/1/10
     **/
    public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
        try {
            T bean = clazz.newInstance();
            BeanUtils.populate(bean, map);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
