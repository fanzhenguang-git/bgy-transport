/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.notificationservice;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.TransportConfigDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dao.entity.TransportConfigEntity;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.transport.saas.qiji.DisplayScreen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 奇迹熙讯信息屏下行通知服务
 *
 * @author: xwb
 * @create: 2019/08/27 16:44
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class ScreenNotificationService implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private DisplayScreen displayScreen;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private TransportConfigDao transportConfigDao;

    @Value("${deliver.product-id.xs-screen}")
    private String productId;

    @PostConstruct
    public void init() {

        String serviceId = registerService.registerNotification(this);
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

        // 查询配置表
        TransportConfigEntity byDeviceId = transportConfigDao.findByDeviceId(deviceId);
        if (byDeviceId == null) {
            log.info("devSn is not exist");
            return;
        }
        // 获取参数
        JSONObject configObject = JSONObject.parseObject(byDeviceId.getConfigValue());
        String hostPort = configObject.getString("hostPort");
        String appId = configObject.getString("appId");
        String appSecret = configObject.getString("appSecret");
        String devSn = configObject.getString("devSn");
        // 控制信息屏开关
        if (DisplayScreen.SCREEN_ON.equals(actionName)) {
            displayScreen.onOffSC(devSn, true, hostPort, appId, appSecret);
        } else if (DisplayScreen.SCREEN_OFF.equals(actionName)) {
            displayScreen.onOffSC(devSn, false, hostPort, appId, appSecret);
        } else if (DisplayScreen.SCREEN_TEXT_OVERLAY.equals(actionName)) {
            if (parameterMap == null) {
                log.info("parame is not correct");
                return;
            }
            TypeValue content = parameterMap.get("content");
            if (content != null) {
                Map<String, Object> temp = new HashMap<>();
                // 整数，协议版本
                temp.put("Version", 1);
                Map<String, Object> textmap = new HashMap<>();
                // 显示效果，支持Scroll（滚动）、和Static（静态）
                textmap.put("Effect", "Fade");
                // 布尔值，是否黑体显示
                textmap.put("Bold", false);
                // 布尔值，是否斜体显示
                textmap.put("Italic", false);
                // 整数，文字前景色，ARGB格式，依次代表透明度和红绿蓝三元色 4294901760 红色
                textmap.put("Foreground", 4294901760L);
                // 整数，文字背景色，格式同上
                textmap.put("Background", 0);
                // 整数，字体大小，像素点，0代表默认
                textmap.put("Size", 16);
                // UTF8显示字幕文字内容
                textmap.put("Content", content.getStringValue());
                JSONObject textObject = new JSONObject(textmap);
                temp.put("Text", textObject);
                JSONObject jsonObject = new JSONObject(temp);
                // 显示位置
                TypeValue bottomValue = parameterMap.get("bottom");
                Boolean isbottom = false;
                if (bottomValue != null) {
                    isbottom = bottomValue.getBoolValue();
                }
                displayScreen.textOverlay(devSn, jsonObject.toJSONString(), isbottom, hostPort, appId, appSecret);
            }
        }
    }
}
