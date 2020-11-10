/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service.impl;

import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.saas.telcom.service.TelcomAdapterService;
import com.sunseaiot.deliver.util.YamlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author hupan
 * @date 2019-09-06.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class TelcomNotificationImpl implements NotificationService {

    @Autowired
    private TelcomAdapterService telcomAdapterService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UpstreamService upstreamService;

    @Autowired
    private DeviceDao deviceDao;

    @PostConstruct
    public void init(){
        Properties properties = YamlUtil.yamlConvertProperties();
        //在yml文件中配置deliver.telcome.product-id.* ，需要上传电信平台的产品ID
        List<Object> telcomeProductList = YamlUtil.matchKeyGetValue(properties, "deliver.telcome.product-id.");
        String serviceId = registerService.registerNotification(this);
        if(telcomeProductList != null && telcomeProductList.size() > 0){
            telcomeProductList.forEach(object -> {
                String productId = object.toString();
                if(!StringUtils.isEmpty(productId)){
                    List<DeviceEntity> deviceEntitylist = deviceDao.findByProductId(productId);
                    deviceEntitylist.stream().forEach(deviceEntity -> registerService.registerDevice(serviceId,deviceEntity.getId()));
                }
            });
        }
    }

    @Override
    public void onOriginData(String deviceId, String payload) {
        log.info("telcom onOriginData");
    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {
        log.info("telcom onMessage");
    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {
        log.info("telcom onAction");

        // 根据设备id，查找appId
        DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth(deviceId, null, null);
        if ((deviceBaseInfo == null)
                || (StringUtils.isEmpty(deviceBaseInfo.getManufacturerId()))) {
            log.warn("unknow device or appId: {}", deviceId);
            return;
        }
        TypeValue typeValue = new TypeValue();
        typeValue.setStringValue(actionName);
        parameterMap.put("actionName", typeValue);

        String appId = deviceBaseInfo.getManufacturerId();
        log.info("telcom post data to device: {}, {}, {}", appId, deviceId, actionName);
        telcomAdapterService.postDataToDevice(appId, deviceId, parameterMap, 0);
    }
}
