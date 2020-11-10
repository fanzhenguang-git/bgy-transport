/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sunseaiot.deliver.dao.*;
import com.sunseaiot.deliver.dao.entity.*;
import com.sunseaiot.deliver.dao.fieldenum.OnlineStatus;
import com.sunseaiot.deliver.dao.fieldenum.TransportDirection;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.rpc.application.ApplicationServiceGrpc.ApplicationServiceBlockingStub;
import com.sunseaiot.deliver.rpc.application.PushOriginDataRequest;
import com.sunseaiot.deliver.service.UpstreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author hupan
 * @date 2019-08-14.
 */
@Slf4j
@Service
public class UpstreamServiceImpl implements UpstreamService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceDataDao deviceDataDao;

    @Autowired
    private DeviceRawDao deviceRawDao;

    @Autowired
    private ProductAttributeDao productAttributeDao;

    @Autowired
    private DeviceDataCacheDao deviceDataCacheDao;

    @Autowired
    private ApplicationServiceBlockingStub applicationServiceBlockingStub;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Value("${spring.kafka.enable}")
    private boolean kafkaEnable;

    @Value("${deliver.grpc.apollo.enable}")
    private boolean serverGrpcEnable;

    private static final String TOPIC = "device-upstream-data";

    @Async
    @Override
    public void postOriginData(String deviceId, String payload) {
        // 查找设备是否存在
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (!optional.isPresent()) {
            log.error("device not exist: {}", deviceId);
            return;
        }
        // 如果开启grpc功能
        if (serverGrpcEnable) {
            try {
                // 保存原始数据
                DeviceRawEntity deviceRawEntity = new DeviceRawEntity();
                deviceRawEntity.setDeviceId(deviceId);
                deviceRawEntity.setContent(payload);
                DeviceRawEntity savedDeviceRawEntity = deviceRawDao.save(deviceRawEntity);
                // 调用grpc接口,将数据推送到感知平台
                PushOriginDataRequest request = PushOriginDataRequest.newBuilder()
                        .setDeviceId(deviceId)
                        .setMessageId(savedDeviceRawEntity.getId())
                        .setPayload(payload)
                        .build();
                applicationServiceBlockingStub.pushOriginData(request);
            } catch (Exception e) {
                log.error("grpc exception: {}", e);
            }
        }
        // 如果开启Kafka功能
        if (kafkaEnable) {
            // 发送设备原始数据到Kafka,其他项目从kafka中获取数据
            JsonObject jsonObject = new JsonParser().parse(payload).getAsJsonObject();
            jsonObject.addProperty("deviceId", deviceId);
            try {
                kafkaTemplate.send(TOPIC, jsonObject.toString());
            } catch (Exception e) {
                log.error("kafkaProducer send postOriginData exception: {}", e);
            }
        }
    }

    @Async
    @Override
    public void postDeviceData(String deviceId, Map<String, TypeValue> attributeMap) {
        // 查找设备是否存在
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (!optional.isPresent()) {
            log.error("device not exist：{}", deviceId);
            return;
        }
        DeviceEntity deviceEntity = optional.get();
        // 获取设备所有属性
        ProductEntity productEntity = deviceEntity.getProduct();
        Map<String, ProductAttributeEntity> nameEntityMap = productAttributeDao.findAttributeNameMapByProductId(productEntity.getId());
        // 遍历新增数据
        Date createTime = new Date();
        List<DeviceDataEntity> deviceDataEntityList = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        attributeMap.forEach((k, v) -> {
            // 检查属性信息是否正确
            ProductAttributeEntity attributeEntity = checkAndGetAttribute(k, v, nameEntityMap);
            if (attributeEntity == null) {
                log.warn("attribute error, skip: {}, {}", deviceId, k);
                return;
            }
            // 构建数据库实体
            DeviceDataEntity deviceDataEntity = createDeviceDataEntity(deviceEntity, attributeEntity, v, createTime);
            deviceDataEntityList.add(deviceDataEntity);
            // json
            addToJsonObject(k, v, jsonObject);
            // 更新到缓存
            deviceDataCacheDao.save(deviceId, attributeEntity.getId(), v, createTime);
        });
        // 存入数据
        deviceDataDao.saveAll(deviceDataEntityList);
    }

    @Async
    @Override
    public void postDeviceStatus(String deviceId, boolean isOnline) {
        // 查找设备是否存在
        Optional<DeviceEntity> optional = deviceDao.findById(deviceId);
        if (!optional.isPresent()) {
            log.error("device not exist: {}", deviceId);
            return;
        }
        DeviceEntity deviceEntity = optional.get();
        // 修改设备状态
        OnlineStatus onlineStatus = isOnline ? OnlineStatus.ONLINE : OnlineStatus.OFFLINE;
        deviceEntity.setOnlineStatus(onlineStatus);
        deviceDao.save(deviceEntity);
    }

    @Override
    public DeviceBaseInfo deviceAuth(String deviceId, String deviceName, String token) {
        DeviceEntity deviceEntity = deviceDao.findByConditions(deviceId, deviceName, token);
        if (deviceEntity == null) {
            return null;
        }
        DeviceBaseInfo deviceBaseInfo = new DeviceBaseInfo();
        deviceBaseInfo.setDeviceId(deviceEntity.getId());
        deviceBaseInfo.setDeviceName(deviceEntity.getName());
        deviceBaseInfo.setManufacturerId(deviceEntity.getManufacturerId());
        return deviceBaseInfo;
    }

    private ProductAttributeEntity checkAndGetAttribute(String key, TypeValue value, Map<String, ProductAttributeEntity> nameEntityMap) {
        // 属性是否存在
        ProductAttributeEntity attributeEntity = nameEntityMap.get(key);
        if (attributeEntity == null) {
            log.debug("unknow attribute name");
            return null;
        }
        // 方向是否正确
        if (attributeEntity.getDirection() == TransportDirection.DOWN) {
            log.debug("attribute direction error");
            return null;
        }
        // 数据类型
        if (attributeEntity.getDataType() != value.getValueCase()) {
            log.debug("attribute data type error, skip: {}, {}");
            return null;
        }
        return attributeEntity;
    }

    private DeviceDataEntity createDeviceDataEntity(DeviceEntity deviceEntity, ProductAttributeEntity attributeEntity
            , TypeValue value, Date createTime) {
        DeviceDataEntity deviceDataEntity = new DeviceDataEntity();
        deviceDataEntity.setDevice(deviceEntity);
        deviceDataEntity.setAttribute(attributeEntity);
        deviceDataEntity.setDirection(TransportDirection.UP);
        switch (value.getValueCase()) {
            case BOOLVALUE:
                deviceDataEntity.setBoolValue(value.getBoolValue());
                break;
            case INTVALUE:
                deviceDataEntity.setIntValue(value.getIntValue());
                break;
            case DOUBLEVALUE:
                deviceDataEntity.setDoubleValue(value.getDoubleValue());
                break;
            case STRINGVALUE:
                deviceDataEntity.setStringValue(value.getStringValue());
                break;
            case OBJECTVALUE:
                deviceDataEntity.setStringValue(value.getObjectValue());
                break;
            default:
                log.warn("unknow type");
                break;
        }
        deviceDataEntity.setCreateTime(createTime);
        return deviceDataEntity;
    }

    private void addToJsonObject(String key, TypeValue value, JsonObject jsonObject) {
        switch (value.getValueCase()) {
            case BOOLVALUE:
                jsonObject.addProperty(key, value.getBoolValue());
                break;
            case INTVALUE:
                jsonObject.addProperty(key, value.getIntValue());
                break;
            case DOUBLEVALUE:
                jsonObject.addProperty(key, value.getDoubleValue());
                break;
            case STRINGVALUE:
                jsonObject.addProperty(key, value.getStringValue());
                break;
            case OBJECTVALUE:
                jsonObject.addProperty(key, value.getObjectValue());
                break;
            default:
                log.warn("unknow type");
                return;
        }
    }

    private void pushDataToApplication(String deviceId, long msgId, String payload) {
        PushOriginDataRequest request = PushOriginDataRequest.newBuilder()
                .setDeviceId(deviceId)
                .setMessageId(msgId)
                .setPayload(payload)
                .build();
        // 调用grpc接口
        try {
            applicationServiceBlockingStub.pushOriginData(request);
        } catch (Exception e) {
            log.error("grpc exception: {}", e);
        }
    }
}
