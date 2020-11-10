/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.notificationservice;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.transport.saas.qiji.Broadcast;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 奇迹平台雷拓广播下行通知服务（暂未使用，后续如果不采用直接调用，采用奇迹接口时可用）
 *
 * @author: xwb
 * @create: 2019/09/11 14:58
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class BroadCastNotificationService implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private Broadcast broadcast;

    @PostConstruct
    public void init() {
//        String serviceId = registerService.registerNotification(this);
//        registerService.registerDevice(serviceId, IotConfig.devSnBroadCast);
    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {

    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {
//        if(!IotConfig.devSnBroadCast.equals(deviceId)){
//            log.info("deviceId is not correct");
//            return;
//        }
//
//        List<String> devSnList = new ArrayList<>();
//        devSnList.add(IotConfig.devSnBroadCast);
//
//        if(Broadcast.BROADCAST_SET_VOLUME.equals(actionName)){
//            TypeValue volume = parameterMap.get("volume");
//            if(volume == null || volume.getIntValue() ==null){
//                log.info("volume is not correct!");
//                return;
//            }
//            //设置广播音量
//            broadcast.setVolume(devSnList,volume.getIntValue().intValue());
//        }else if (Broadcast.BROADCAST_PLAY_PROGRAME.equals(actionName)){
//            TypeValue fileId = parameterMap.get("fileId");
//            if(fileId == null || fileId.getStringValue() == null){
//                log.info("fileId is empty!");
//            }
//            //播放节目
//            List<String> fileIds = new ArrayList<>();
//            broadcast.playFile(IotConfig.devSnBroadCast,fileIds);
//        }else if(Broadcast.BROADCAST_STOP_PALY_PROGRAME.equals(actionName)){
//            TypeValue volume = parameterMap.get("sid");
//            if(volume == null || volume.getIntValue() ==null){
//                log.info("sid is empty!");
//                return;
//            }
//
//            //停止播放
//            broadcast.stop(IotConfig.devSnBroadCast,volume.getStringValue());
//        }
    }
}
