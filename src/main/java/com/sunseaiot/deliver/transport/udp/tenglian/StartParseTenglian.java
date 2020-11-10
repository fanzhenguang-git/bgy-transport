/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.tenglian;

import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.udp.UpstreamPlatform;
import com.sunseaiot.deliver.transport.udp.redis.UdpOperateCacheDao;
import com.sunseaiot.deliver.transport.udp.redis.entity.UdpOperateEntity;
import com.sunseaiot.deliver.transport.udp.util.UdpTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 腾联udp产品开始解析类
 *
 * @author qixiaofei
 * @date 2019-09-23.
 */
@Slf4j
@Component
public class StartParseTenglian {

    @Autowired
    UpstreamPlatform upstreamPlatform;

    @Autowired
    UdpOperateCacheDao udpOperateCacheDao;

    @Autowired
    UpstreamService upstreamService;


    public String parseMessage(byte[] bytes) {

        log.debug("start to parse tenglian message");
        // 根据文档，当数据长度为20,17,2的时候，不需要解析
        if (bytes.length == 20 || bytes.length == 27 || bytes.length == 2) {
            return null;
        }

        // 校验
        boolean result = UdpTools.tenglianVerification(bytes);
        if (!result) {
            log.debug("message verification failed");
            return null;
        }

        // 获取报文中关于设备的基本信息
        Map<String, String> basicInfoFromMessage = UdpTools.getBasicInfoFromMessage(bytes);
        String addressPlatform = basicInfoFromMessage.get("addressPlatform");

        // 设备是否存在
        DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth(null, addressPlatform, null);
        if (null == deviceBaseInfo) {
            log.debug("can't find device by address={}", addressPlatform);
            return null;
        }
        log.debug("message verification success");

        // 解析
        Map<String, TypeValue> typeValueMap = ResolveByWord.resolve(bytes);
        typeValueMap.put("serial", new TypeValue().setStringValue(addressPlatform));


       	

        // 上传感知平台
        upstreamPlatform.upstreamToPlatform(deviceBaseInfo, typeValueMap);

        // 查询redis中缓存的操作指令并删除
        UdpOperateEntity udpOperateEntity = udpOperateCacheDao.findLatest(addressPlatform);
        udpOperateCacheDao.delete(addressPlatform);

        // 打包
        return PackMessage.packMessage(basicInfoFromMessage, udpOperateEntity);
    }
}
