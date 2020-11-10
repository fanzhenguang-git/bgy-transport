/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.service.trash.impl;

import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import com.google.gson.JsonObject;
import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.service.impl.AepServiceImpl;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;
import com.sunseaiot.deliver.util.RestUtils;
import com.sunseaiot.deliver.vo.RemoteInstructionCreateVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * aep-垃圾桶下行
 *
 * @Author: wanglong
 * @Date: 2019/12/24
 **/
@Slf4j
@Component
public class TrashDownServiceImpl extends AepServiceImpl implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UpstreamService upstreamService;

    @Getter
    private String serviceId;

    @Autowired
    private DeviceDao deviceDao;

    @Value("${deliver.product-id.aep-trash}")
    private String productId;

    @PostConstruct
    public void init() {

        this.serviceId = registerService.registerNotification(this);
        // 注册设备
        List<DeviceEntity> deviceEntitylist = deviceDao.findByProductId(productId);
        deviceEntitylist.stream().forEach(deviceEntity -> registerService.registerDevice(serviceId, deviceEntity.getId()));
    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {

    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {
        log.info("aep-垃圾桶下发指令接口被调用。。。。。。。。。。指令名称为： {}", actionName);
        // 获取设备id
        DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth(deviceId, "", "");
        if (null == deviceBaseInfo) {
            log.error("Device is null");
            return;
        }
        try {
            //下发指令
            RemoteInstructionCreateVO remoteInstructionCreateVO = new RemoteInstructionCreateVO();
            remoteInstructionCreateVO.setDeviceId(deviceId);
            remoteInstructionCreateVO.setOperator("admin");
            remoteInstructionCreateVO.setProductId(10031306);
            remoteInstructionCreateVO.setTtl(20);
            JsonObject content = new JsonObject();
            JsonObject params = new JsonObject();
            for (String key : parameterMap.keySet()) {
                if ("msgID".equals(key)) {
                    params.addProperty(key, parameterMap.get(key).getIntValue() % 255);
                } else if ("msgSerialNum".equals(key) || "msgCheckSum".equals(key)) {
                    params.addProperty(key, parameterMap.get(key).getIntValue() % 65535);
                } else if ("property".equals(key)) {
                    params.addProperty(key, parameterMap.get(key).getIntValue() % 35535);
                } else {
                    params.addProperty(key, parameterMap.get(key).getIntValue());
                }
            }
            content.add("params", params);
            content.addProperty("serviceIdentifier", actionName);
            remoteInstructionCreateVO.setContent(content);
            byte[] bytes = RestUtils.convert2Bytes(remoteInstructionCreateVO);
            this.addInstruction("97c3b526eefe4cc9a5474e977b60a488", bytes);
        } catch (Exception e) {
            log.error("TrashDownServiceImpl ERROR ：{}", e);
        }

    }

    public Object addInstruction(String masterKey, byte[] bytes) throws Exception {
        CreateCommandRequest request = new CreateCommandRequest();
        request.addParamMasterKey(masterKey);
        request.setBody(bytes);
        CreateCommandResponse response = buildAepCommand().CreateCommand(request);
        log.info("AEP 平台垃圾桶下发指令返回信息：{}", response.toString());
        return RestUtils.wrap(response);
    }

    @Override
    public void saveBusiness(PostDataChangeVo postDataChangeVo) {

    }

    @Override
    public void saveEvent(PostDataChangeVo postDataChangeVo) {

    }
}
