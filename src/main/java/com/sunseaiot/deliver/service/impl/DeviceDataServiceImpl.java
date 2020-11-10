/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service.impl;

import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.DeviceDataCacheDao;
import com.sunseaiot.deliver.dao.DeviceDataDao;
import com.sunseaiot.deliver.dao.ProductAttributeDao;
import com.sunseaiot.deliver.dao.entity.DeviceDataEntity;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dao.entity.ProductAttributeEntity;
import com.sunseaiot.deliver.dao.entity.ProductEntity;
import com.sunseaiot.deliver.dao.fieldenum.OnlineStatus;
import com.sunseaiot.deliver.dto.AttributeKVTs;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.rpc.basedata.KeyValueProto;
import com.sunseaiot.deliver.rpc.basedata.KeyValueType;
import com.sunseaiot.deliver.rpc.device.DeviceStatus;
import com.sunseaiot.deliver.service.DeviceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hupan
 * @date 2019-08-15.
 */
@Slf4j
@Component
public class DeviceDataServiceImpl implements DeviceDataService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private ProductAttributeDao productAttributeDao;

    @Autowired
    private DeviceDataDao deviceDataDao;

    @Autowired
    private DeviceDataCacheDao deviceDataCacheDao;

    @Override
    public DeviceStatus getDeviceStatus(String deviceId) {

        // 查找设备和属性
        DeviceEntity deviceEntity =
                deviceDao.findById(deviceId).orElseThrow(() -> new DeliverException(DeliverError.DEVICE_NOT_EXIST));
        if (deviceEntity.getOnlineStatus() == OnlineStatus.ONLINE) {
            return DeviceStatus.ON_LINE;
        } else {
            return DeviceStatus.OFF_LINE;
        }
    }

    @Override
    public List<KeyValueProto> getDeviceHistoryAttribute(String deviceId, String attributeKey, long startTime, long stopTime) {
        // 查找设备和属性
        DeviceEntity deviceEntity =
                deviceDao.findById(deviceId).orElseThrow(() -> new DeliverException(DeliverError.DEVICE_NOT_EXIST));
        String productId = deviceEntity.getProduct().getId();
        ProductAttributeEntity attributeEntity = productAttributeDao.findByProductIdAndName(productId, attributeKey).orElseThrow(() -> new DeliverException(DeliverError.DEVICE_NOT_EXIST));
        String attributeId = attributeEntity.getId();
        List<DeviceDataEntity> deviceDataEntityList = deviceDataDao.findAllByAttributeIdAndCreateTime(attributeId,
                new Date(startTime), new Date(stopTime));
        List<KeyValueProto> protoList = new ArrayList<>();
        for (DeviceDataEntity deviceDataEntity : deviceDataEntityList) {
            KeyValueProto proto = entityToKeyValueProto(attributeEntity, deviceDataEntity);
            protoList.add(proto);
        }
        return protoList;
    }

    @Override
    public List<KeyValueProto> getAllLatestAttribute(String deviceId) {
        // 查找设备是否存在
        DeviceEntity deviceEntity = deviceDao.findById(deviceId).orElseThrow(() -> new DeliverException(DeliverError.DEVICE_NOT_EXIST));
        // 获取设备所有属性
        ProductEntity productEntity = deviceEntity.getProduct();
        List<ProductAttributeEntity> attributeEntityList = productAttributeDao.findAllByProductId(productEntity.getId());
        // 获取每个属性的最新值
        List<KeyValueProto> keyValueProtoList = new ArrayList<>();
        for (ProductAttributeEntity attributeEntity : attributeEntityList) {
            DeviceDataEntity deviceDataEntity = deviceDataCacheDao.findLatest(deviceId, attributeEntity.getId(),
                    attributeEntity.getDataType());
            if (deviceDataEntity == null) {
                log.warn("this attribute has no data: {}, {}", deviceId, attributeEntity.getId());
                continue;
            }
            KeyValueProto proto = entityToKeyValueProto(attributeEntity, deviceDataEntity);
            keyValueProtoList.add(proto);
        }
        return keyValueProtoList;
    }

    private AttributeKVTs entityToValueTs(ProductAttributeEntity attributeEntity, DeviceDataEntity dataEntity) {
        AttributeKVTs kVTs = new AttributeKVTs();
        kVTs.setKey(attributeEntity.getName());
        kVTs.setKeyName(attributeEntity.getDisplayName());
        kVTs.setTimestamp(dataEntity.getCreateTime().getTime());
        TypeValue value = new TypeValue();
        switch (attributeEntity.getDataType()) {
            case BOOLVALUE:
                value.setBoolValue(dataEntity.getBoolValue());
                break;
            case INTVALUE:
                value.setIntValue(dataEntity.getIntValue());
                break;
            case DOUBLEVALUE:
                value.setDoubleValue(dataEntity.getDoubleValue());
                break;
            case STRINGVALUE:
                value.setStringValue(dataEntity.getStringValue());
                break;
            case OBJECTVALUE:
                value.setObjectValue(dataEntity.getStringValue());
                break;
            default:
                log.warn("type error");
        }
        kVTs.setTypeValue(value);
        return kVTs;
    }

    private KeyValueProto entityToKeyValueProto(ProductAttributeEntity attributeEntity, DeviceDataEntity dataEntity) {
        KeyValueProto.Builder builder = KeyValueProto.newBuilder();
        builder.setKey(attributeEntity.getName());
        builder.setKeyName(attributeEntity.getDisplayName());
        builder.setTimestamp(dataEntity.getCreateTime().getTime());
        switch (attributeEntity.getDataType()) {
            case BOOLVALUE:
                builder.setType(KeyValueType.BOOLEAN_V);
                builder.setBoolValue(dataEntity.getBoolValue());
                break;
            case INTVALUE:
                builder.setType(KeyValueType.LONG_V);
                builder.setLongValue(dataEntity.getIntValue());
                break;
            case DOUBLEVALUE:
                builder.setType(KeyValueType.DOUBLE_V);
                builder.setDoubleValue(dataEntity.getDoubleValue());
                break;
            case STRINGVALUE:
                builder.setType(KeyValueType.STRING_V);
                builder.setStringValue(dataEntity.getStringValue());
                break;
            case OBJECTVALUE:
                builder.setType(KeyValueType.OBJECT_V);
                builder.setStringValue(dataEntity.getStringValue());
                break;
            default:
                log.warn("type error");
        }
        return builder.build();
    }
}
