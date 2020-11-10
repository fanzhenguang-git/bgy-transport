/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.mqtt.msj;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.constant.MqttBrokerConstant;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hupan
 * @date 2019-08-13.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.msj.enable", havingValue = "true")
public class MsjTransprotService {

    /**
     * date format
     */
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 电表编号
     */
    private static String DB_NUMBER = "108";

    /**
     * 温湿度编号
     */
    private static String WSD_NUMBER = "110";

    /**
     * 智能动环监控设备编号
     */
    private static String ZNHD_NUMBER = "117";

    /**
     * 电表id
     */
    private static String DB_ID = "39b1acfa-bf48-11e9-a610-0242ac110004";

    /**
     * 温湿度id
     */
    private static String WSD_ID = "3bf9b0b5-bf48-11e9-a610-0242ac110004";

    /**
     * 智能东环监控设备id
     */
    private static String ZNHD_ID = "3a78d9cd-c222-11e9-a610-0242ac110004";

    /**
     * 网关id
     */
    private static String WG_ID = "9749bf18-bf46-11e9-a610-0242ac110004";


    @Autowired
    private UpstreamService upstreamService;

    /**
     * 麦斯杰设备数据上行
     *
     * @param topic   主题
     * @param payLoad 消息
     */
    public void analysisMessage(String topic, String payLoad) {
        JSONObject jsonObject = JSONObject.parseObject(payLoad);
        JSONObject data = jsonObject.getJSONObject("data");
        if (null == data) {
            log.warn("jsonObject data is null, skip: {}, {}", payLoad);
            return;
        }
        // 获取设备id
        List<String> deviceIdList = getDeviceIdList();
        if (null == deviceIdList || deviceIdList.size() < 1) {
            log.warn("deviceIdList is null");
            return;
        }
        if (MqttBrokerConstant.TOPIC_PUB_IN_TIME_DATA.equals(topic)) {
            JSONArray list = data.getJSONArray("list");
            if (null == list || list.size() < 1) {
                log.warn("deviceData is null");
                return;
            }
            sendData(deviceIdList, list);
        } else if (MqttBrokerConstant.TOPIC_PUB_DEVICE_STATES.equals(topic)) {
            int state = data.getInteger("state");
            Map<String, TypeValue> attributeValueMap = new HashMap<String, TypeValue>();
            try {
                attributeValueMap.put("version", getTypeValueValue(data.getString("version")));
                attributeValueMap.put("ram", getTypeValueValue(data.getString("ram")));
                attributeValueMap.put("cpu", getTypeValueValue(data.getString("cpu")));
                attributeValueMap.put("ip", getTypeValueValue(data.getString("ip")));
                attributeValueMap.put("lastconnect", getTypeValueValue(data.getString("lastconnect")));
                attributeValueMap.put("lastsent", getTypeValueValue(data.getString("lastsent")));
                attributeValueMap.put("packCount",
                        getTypeValueValue(StringUtils.isEmpty(data.getString("packCount")) ? "0" : data.getString(
                                "packCount")));
                attributeValueMap.put("lossRate", getTypeValueValue(data.getString("lossRate")));
                upstreamService.postDeviceData(WG_ID, attributeValueMap);
                upstreamService.postDeviceStatus(WG_ID, state == 1 ? true : false);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new DeliverException(DeliverError.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * 获取设备ID
     */
    private List<String> getDeviceIdList() {
        List<String> list = new ArrayList<>();
        // 电表
        list.add(DB_ID);
        // 温湿度
        list.add(WSD_ID);
        // 智能动环监控设备
        list.add(ZNHD_ID);
        return list;
    }

    private TypeValue getTypeValueValue(String value) {
        TypeValue attributeValue = new TypeValue();
        if (StringUtil.isDateString(value, DATE_FORMAT)) {
            attributeValue.setStringValue(value);
        } else if (StringUtil.isDouble(value)) {
            attributeValue.setDoubleValue(Double.valueOf(value));
        } else if (StringUtil.isInteger(value)) {
            attributeValue.setIntValue(Integer.valueOf(value));
        } else if (value.equals("true") || value.equals("false")) {
            boolean val = value.equals("true") ? true : false;
            attributeValue.setBoolValue(val);
        } else {
            attributeValue.setStringValue(value);
        }
        return attributeValue;
    }

    private void sendData(List<String> deviceIdList, JSONArray jsonArray) {
        for (String deviceId : deviceIdList) {
            Map<String, TypeValue> attributeValueMap = new HashMap<String, TypeValue>();
            for (Object obj : jsonArray) {
                JSONObject jsonObj = (JSONObject) obj;
                // 截取信号id，获取设备编号
                String signalNumber = jsonObj.getString("signalid").split("-")[0];
                // 获取信号id
                String signalId = jsonObj.getString("signalid").split("-")[1];
                // 获取值
                String originVal = jsonObj.getString("originval");
                TypeValue attributeValue = getTypeValueValue(originVal);
                // 电表
                if (DB_ID.equals(deviceId) && DB_NUMBER.equals(signalNumber)) {
                    attributeValueMap.put(signalId, attributeValue);
                }
                // 温湿度
                if (WSD_ID.equals(deviceId) && WSD_NUMBER.equals(signalNumber)) {
                    attributeValueMap.put(signalId, attributeValue);
                }
                // 智能动环监控设备
                if (ZNHD_ID.equals(deviceId) && ZNHD_NUMBER.equals(signalNumber)) {
                    attributeValueMap.put(signalId, attributeValue);
                }
            }
            upstreamService.postDeviceData(deviceId, attributeValueMap);
        }
    }
}

