/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.service.geo.impl;

import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import com.google.gson.JsonObject;
import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.service.impl.AepServiceImpl;
import com.sunseaiot.deliver.transport.saas.aep.service.GeoService;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;
import com.sunseaiot.deliver.util.RestUtils;
import com.sunseaiot.deliver.vo.GeoCommand;
import com.sunseaiot.deliver.vo.RemoteInstructionCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AEP 地磁实现
 *
 * @author hexing
 * @date 2019/12/25 13:50
 */
@Slf4j
@Service
public class GeoServiceImpl extends AepServiceImpl implements NotificationService, GeoService {
    @Autowired
    private RegisterService registerService;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private UpstreamService upstreamService;

    @Value("${deliver.aep.geo-lwm2m-product-id}")
    private String productId;

    @Value("${deliver.aep.geo-lwm2m-master-key}")
    private String masterKey;

    @PostConstruct
    public void init() {
        String serviceId = registerService.registerNotification(this);
        List<DeviceEntity> deviceEntityList = deviceDao.findByProductId(productId);
        deviceEntityList.stream().forEach(deviceEntity -> registerService.registerDevice(serviceId, deviceEntity.getId()));
    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {
        try {
            //下发指令
            RemoteInstructionCreateVO remoteInstructionCreateVO = new RemoteInstructionCreateVO();
            remoteInstructionCreateVO.setDeviceId(deviceId);
            remoteInstructionCreateVO.setOperator("admin");
            remoteInstructionCreateVO.setProductId(Integer.parseInt(productId));
            remoteInstructionCreateVO.setTtl(3);

            JsonObject content = new JsonObject();
            JsonObject params = new JsonObject();
            params.addProperty("command_type", GeoCommand.valueOf(actionName).getCode());
            content.add("params", params);
            content.addProperty("serviceIdentifier", "device_control");
            remoteInstructionCreateVO.setContent(content);
            byte[] bytes = RestUtils.convert2Bytes(remoteInstructionCreateVO);
            this.addInstruction(masterKey, bytes);
        } catch (Exception e) {
            log.error("GeoServiceImpl ERROR ", e);
        }
    }

    @Override
    public Object addInstruction(String masterKey, byte[] bytes) throws Exception {
        CreateCommandRequest request = new CreateCommandRequest();
        request.addParamMasterKey(masterKey);
        request.setBody(bytes);
        CreateCommandResponse response = buildAepCommand().CreateCommand(request);
        log.info("AEP 平台地磁下发指令返回信息：{}", response.toString());
        return RestUtils.wrap(response);
    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {

    }

    @Override
    public void saveBusiness(PostDataChangeVo postDataChangeVo) {
        Map<String, TypeValue> attributeMap = new HashMap<>(16);
        attributeMap.put("battery_voltage", new TypeValue().setDoubleValue(Double.valueOf(postDataChangeVo.getPayload().get("battery_voltage").toString())));
        attributeMap.put("battery_value", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("battery_value").toString())));
        attributeMap.put("ptime", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("ptime").toString())));
        attributeMap.put("parking_state", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("parking_state").toString())));
        attributeMap.put("error_code", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("error_code").toString())));
        attributeMap.put("parking_change", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("parking_change").toString())));
        attributeMap.put("magnetic_value", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("magnetic_value").toString())));
        upstreamService.postDeviceData(postDataChangeVo.getDeviceId(), attributeMap);
    }

    @Override
    public void saveEvent(PostDataChangeVo postDataChangeVo) {

    }
}
