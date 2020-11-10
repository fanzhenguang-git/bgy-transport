/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.impl;

import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dao.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DeviceDao实现
 *
 * @author hupan
 * @date 2019-08-14.
 */
@Component
public class DeviceDaoImpl implements DeviceDao {

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public Optional<DeviceEntity> findById(String deviceId) {
        return deviceRepository.findById(deviceId);
    }

    @Override
    public DeviceEntity findByConditions(String deviceId, String deviceName, String token) {
        // 参数校验
        if ((StringUtils.isEmpty(deviceId)) && (StringUtils.isEmpty(deviceName))) {
            return null;
        }
        Specification<DeviceEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(deviceId)) {
                predicates.add(criteriaBuilder.equal(root.get("id"), deviceId));
            }
            if (!StringUtils.isEmpty(deviceName)) {
                predicates.add(criteriaBuilder.equal(root.get("name"), deviceName));
            }
            if (!StringUtils.isEmpty(token)) {
                predicates.add(criteriaBuilder.equal(root.get("token"), token));
            }
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
            return criteriaQuery.getRestriction();
        };

        List<DeviceEntity> deviceEntityList = deviceRepository.findAll(specification);
        if (deviceEntityList.isEmpty()) {
            return null;
        } else {
            return deviceEntityList.get(0);
        }
    }

    @Override
    public DeviceEntity save(DeviceEntity deviceEntity) {
        return deviceRepository.save(deviceEntity);
    }

    @Override
    public List<DeviceEntity> findByProductId(String productId) {
        // 参数校验
        if (StringUtils.isEmpty(productId)) {
            return null;
        }
        Specification<DeviceEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("product").get("id"), productId));
            criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
            return criteriaQuery.getRestriction();
        };
        List<DeviceEntity> deviceEntityList = deviceRepository.findAll(specification);
        return deviceEntityList;
    }
}
