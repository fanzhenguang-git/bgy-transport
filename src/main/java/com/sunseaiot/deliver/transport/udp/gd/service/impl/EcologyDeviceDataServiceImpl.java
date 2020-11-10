/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.sunseaiot.deliver.constant.RedisConstant;
import com.sunseaiot.deliver.dao.entity.DeviceDataEntity;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dao.entity.ProductAttributeEntity;
import com.sunseaiot.deliver.dao.fieldenum.TransportDirection;
import com.sunseaiot.deliver.dao.repository.DeviceDataRepository;
import com.sunseaiot.deliver.dao.repository.DeviceRepository;
import com.sunseaiot.deliver.dao.repository.ProductAttributeRepository;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.udp.gd.bean.DeviceDataDTO;
import com.sunseaiot.deliver.transport.udp.gd.bean.NorthDeviceDataDTO;
import com.sunseaiot.deliver.transport.udp.gd.dao.entity.DeviceLoRaEntity;
import com.sunseaiot.deliver.transport.udp.gd.dao.repository.DeviceLoRaRepository;
import com.sunseaiot.deliver.transport.udp.gd.service.EcologyDeviceDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 智慧生态设备数据service层
 *
 * @author weishaopeng
 * @date 2019/12/19 16:55
 */
@Slf4j
@Service
public class EcologyDeviceDataServiceImpl implements EcologyDeviceDataService {

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DeviceLoRaRepository deviceLoRaRepository;

    @Autowired
    private UpstreamService upstreamService;

    @Override
    public void saveEcologyDeviceData(List<DeviceDataDTO> deviceDataDTOList) {
        // 当上传的数据不为null的时候
        if (null != deviceDataDTOList && deviceDataDTOList.size() > 0) {
            deviceDataDTOList.forEach(item -> {
                // 校验该设备存不存在
                List<DeviceEntity> deviceEntityList = deviceRepository.findAllByName(item.getGwEui() + item.getDeviceSerial());
                if (null != deviceEntityList && deviceEntityList.size() == 1) {
                    // 校验属性1存不存在
                    Optional<ProductAttributeEntity> productAttributeEntityOptional = productAttributeRepository.findByProductIdAndName(deviceEntityList.get(0).getProduct().getId(), item.getAttributeName1());
                    if (productAttributeEntityOptional.isPresent()) {
                        saveDeviceDataEntity(item, item.getUpValue(), deviceEntityList.get(0), productAttributeEntityOptional.get());
                    } else {
                        log.error("[{}] productAttribute1 is not unique or is empty", item.getAttributeName1());
                    }
                    if (!StringUtils.isEmpty(item.getAttributeName2())) {
                        // 校验属性2存不存在
                        Optional<ProductAttributeEntity> productAttributeEntityOptional2 = productAttributeRepository.findByProductIdAndName(deviceEntityList.get(0).getProduct().getId(), item.getAttributeName2());
                        if (productAttributeEntityOptional2.isPresent()) {
                            saveDeviceDataEntity(item, item.getUpValue2(), deviceEntityList.get(0), productAttributeEntityOptional2.get());
                        } else {
                            log.error("[{}] productAttribute2 is not unique or is empty", item.getAttributeName2());
                        }
                    }
                } else {
                    log.error("[{}] deviceSerial is not unique or is empty", item.getGwEui() + item.getDeviceSerial());
                }
            });
        }
    }

    /**
     * 保存设备数据到deviceData表
     *
     * @param deviceDataDTO          LoRa网关传过来后封装的设备相关数据
     * @param doubleValue            该属性对应的值
     * @param deviceEntity           对应的设备
     * @param productAttributeEntity 对应的产品属性
     */
    private void saveDeviceDataEntity(DeviceDataDTO deviceDataDTO, Double doubleValue, DeviceEntity deviceEntity, ProductAttributeEntity productAttributeEntity) {

        DeviceDataEntity deviceDataEntity = new DeviceDataEntity();
        deviceDataEntity.setDoubleValue(doubleValue);
        deviceDataEntity.setCreateTime(new Date());
        deviceDataEntity.setAttribute(productAttributeEntity);
        deviceDataEntity.setDevice(deviceEntity);
        deviceDataEntity.setDirection(TransportDirection.UP);
        try {
            // 清除redis缓存
            redisTemplate.delete(RedisConstant.DEVICE_LATEST_DATA + deviceEntity.getId());
            // 保存到数据库
            deviceDataRepository.save(deviceDataEntity);
            // 保存设备网关数据
            saveDeviceLoRaEntity(deviceDataDTO);
            // 调用gRpc
            NorthDeviceDataDTO northDeviceDataDTO = new NorthDeviceDataDTO();
            BeanUtils.copyProperties(deviceDataEntity, northDeviceDataDTO);
            northDeviceDataDTO.setTime(deviceDataDTO.getTime());
            String payload = JSONArray.toJSON(northDeviceDataDTO).toString();
            upstreamService.postOriginData(deviceEntity.getId(), payload);
        } catch (Exception e) {
            log.error("cleanRedisCache or saveDeviceData or saveDeviceLoRa or invoke gRpc error: {}", e.getMessage());
        }
    }

    /**
     * 保存设备网关数据
     *
     * @param deviceDataDTO 设备数据DTO
     */
    private void saveDeviceLoRaEntity(DeviceDataDTO deviceDataDTO) {
        List<DeviceEntity> deviceEntityList = deviceRepository.findAllByName(deviceDataDTO.getGwEui() + deviceDataDTO.getDeviceSerial());
        if (null != deviceEntityList && deviceEntityList.size() == 1) {
            List<DeviceLoRaEntity> deviceLoRaEntityList = deviceLoRaRepository.findAllByDeviceSerialAndGwEui(deviceDataDTO.getDeviceSerial(), deviceDataDTO.getGwEui());
            // 如果该设备找不到对应网关数据
            if (null == deviceLoRaEntityList || deviceLoRaEntityList.size() == 0) {
                DeviceLoRaEntity deviceLoRaEntity = new DeviceLoRaEntity();
                BeanUtils.copyProperties(deviceDataDTO, deviceLoRaEntity);
                deviceLoRaEntity.setCreateTime(new Date());
                deviceLoRaRepository.save(deviceLoRaEntity);
            } else {
                DeviceLoRaEntity deviceLoRaEntity = deviceLoRaEntityList.get(0);
                deviceLoRaEntity.setUpData(deviceDataDTO.getUpData());
                deviceLoRaRepository.save(deviceLoRaEntity);
            }
        } else {
            log.error("[{}] deviceSerial is not unique or is empty", deviceDataDTO.getGwEui() + deviceDataDTO.getDeviceSerial());
        }
    }
}
