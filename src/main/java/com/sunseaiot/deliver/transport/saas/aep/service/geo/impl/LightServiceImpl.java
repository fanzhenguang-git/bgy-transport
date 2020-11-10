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
import com.sunseaiot.deliver.transport.saas.aep.service.LightService;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;
import com.sunseaiot.deliver.util.RestUtils;
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
 * 灯控service层
 *
 * @author hexing
 * @date 2020/1/17 09:58
 */
@Service
@Slf4j
public class LightServiceImpl extends AepServiceImpl implements NotificationService, LightService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private UpstreamService upstreamService;

    @Value("${deliver.product-id.aep-light}")
    private String productId;

    @Value("${deliver.master-apikey.aep-light}")
    private String masterKey;

    @PostConstruct
    public void init() {
        String serviceId = registerService.registerNotification(this);
        List<DeviceEntity> deviceEntityList = deviceDao.findByProductId(productId);
        deviceEntityList.stream().forEach(deviceEntity -> registerService.registerDevice(serviceId, deviceEntity.getId()));
    }

    @Override
    public void saveBusiness(PostDataChangeVo postDataChangeVo) {
        Map<String, TypeValue> attributeMap = new HashMap<>(16);
        attributeMap.put("onoff", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("onoff").toString())));
        upstreamService.postDeviceData(postDataChangeVo.getDeviceId(), attributeMap);
    }

    @Override
    public void saveEvent(PostDataChangeVo postDataChangeVo) {

    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {

    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) throws Exception {
        RemoteInstructionCreateVO remoteInstructionCreateVO = new RemoteInstructionCreateVO();
        remoteInstructionCreateVO.setDeviceId(deviceId);
        remoteInstructionCreateVO.setOperator("admin");
        remoteInstructionCreateVO.setProductId(Integer.parseInt(productId));
        // 缓存时间3 s
        remoteInstructionCreateVO.setTtl(3);

        JsonObject content = new JsonObject();
        JsonObject params = new JsonObject();
        params.addProperty("onoff", actionName);
        content.add("params", params);
        content.addProperty("serviceIdentifier", "set_onoff");
        remoteInstructionCreateVO.setContent(content);
        byte[] bytes = RestUtils.convert2Bytes(remoteInstructionCreateVO);
        this.addInstruction(masterKey, bytes);
    }

    @Override
    public Object addInstruction(String masterKey, byte[] bytes) throws Exception {
        CreateCommandRequest request = new CreateCommandRequest();
        request.addParamMasterKey(masterKey);
        request.setBody(bytes);
        CreateCommandResponse response = buildAepCommand().CreateCommand(request);
        return RestUtils.wrap(response);
    }
}
