/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.service.smoke.impl;

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
import com.sunseaiot.deliver.transport.saas.aep.service.SmokeSDService;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;
import com.sunseaiot.deliver.util.RestUtils;
import com.sunseaiot.deliver.vo.RemoteInstructionCreateVO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hexing
 * @date 2019/12/30 11:16
 */
@Slf4j
@Service
public class SmokeSDServiceImpl extends AepServiceImpl implements NotificationService, SmokeSDService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private DeviceDao deviceDao;

    @Getter
    private String serviceId;

    @Value("${deliver.product-id.aep-smoke-rhwl-sd}")
    private String productId;

    @Value("${deliver.master-apikey.aep-smoke-rhwl-sd}")
    private String masterKey;

    @Autowired
    private UpstreamService upstreamService;

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
        try {
            //下发指令
            RemoteInstructionCreateVO remoteInstructionCreateVO = new RemoteInstructionCreateVO();
            remoteInstructionCreateVO.setDeviceId(deviceId);
            remoteInstructionCreateVO.setOperator("admin");
            remoteInstructionCreateVO.setProductId(Integer.parseInt(productId));
            remoteInstructionCreateVO.setTtl(3);
            JsonObject content = new JsonObject();
            JsonObject params = new JsonObject();
            if (parameterMap.size() < 0) {
                log.error(" ERROR:命令参数不全");
                return;
            }
            long valueData = parameterMap.get(actionName).getIntValue();
            if (valueData < 0 || valueData > 1) {
                throw new RuntimeException("类型错误.");
            }
            params.addProperty("muffling", valueData);
            content.add("params", params);
            content.addProperty("serviceIdentifier", "cmd");
            remoteInstructionCreateVO.setContent(content);
            byte[] bytes = RestUtils.convert2Bytes(remoteInstructionCreateVO);
            this.addInstruction(masterKey, bytes);
        } catch (Exception e) {
            log.error(" ERROR ", e);
        }
    }

    @Override
    public Object addInstruction(String masterKey, byte[] bytes) throws Exception {
        CreateCommandRequest request = new CreateCommandRequest();
        request.addParamMasterKey(masterKey);
        request.setBody(bytes);
        CreateCommandResponse response = buildAepCommand().CreateCommand(request);
        log.info("AEP 平台烟感--RHWL-SD(JD-SD51）下发指令返回信息：{}", response.toString());
        return RestUtils.wrap(response);
    }

    @Override
    public void saveBusiness(PostDataChangeVo postDataChangeVo) {
        Map<String, TypeValue> attributeMap = new HashMap<>(16);
        if (postDataChangeVo.getPayload().containsKey("smoke_state")) {
            attributeMap.put("smoke_state", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("smoke_state").toString())));
        }
        if (postDataChangeVo.getPayload().containsKey("smoke_value")) {
            attributeMap.put("smoke_value", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("smoke_value").toString())));
        }
        if (postDataChangeVo.getPayload().containsKey("tamper_alarm")) {
            attributeMap.put("tamper_alarm", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("tamper_alarm").toString())));
        }
        if (postDataChangeVo.getPayload().containsKey("error")) {
            attributeMap.put("error", new TypeValue().setIntValue(Long.parseLong(postDataChangeVo.getPayload().get("error").toString())));
        }
        attributeMap.put("battery_value", new TypeValue().setIntValue(Long.parseLong((postDataChangeVo.getPayload().get("battery_value").toString()))));
        attributeMap.put("signalPower", new TypeValue().setIntValue(Long.parseLong((postDataChangeVo.getPayload().get("signalPower").toString()))));
        attributeMap.put("snr", new TypeValue().setIntValue(Long.parseLong((postDataChangeVo.getPayload().get("snr").toString()))));
        attributeMap.put("RSRP", new TypeValue().setIntValue(Long.parseLong((postDataChangeVo.getPayload().get("RSRP").toString()))));
        attributeMap.put("tx_Power", new TypeValue().setIntValue(Long.parseLong((postDataChangeVo.getPayload().get("tx_Power").toString()))));
        attributeMap.put("cell_ID", new TypeValue().setIntValue(Long.parseLong((postDataChangeVo.getPayload().get("cell_ID").toString()))));
        attributeMap.put("IMSI", new TypeValue().setStringValue(postDataChangeVo.getPayload().get("IMSI").toString()));
        attributeMap.put("IMEI", new TypeValue().setStringValue(postDataChangeVo.getPayload().get("IMEI").toString()));
        upstreamService.postDeviceData(postDataChangeVo.getDeviceId(), attributeMap);
    }

    @Override
    public void saveEvent(PostDataChangeVo postDataChangeVo) {
    }
}
