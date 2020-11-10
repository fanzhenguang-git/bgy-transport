/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.tenglian;

import com.sunseaiot.deliver.transport.udp.redis.entity.UdpOperateEntity;
import com.sunseaiot.deliver.transport.udp.util.UdpTools;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 腾联打包
 *
 * @author qixiaofei
 * @date 2019-09-23.
 */
@Slf4j
public class PackMessage {

    public static String packMessage(Map<String, String> map, UdpOperateEntity udpOperateEntity) {
        String fnValue = map.get("fnValue");
        if (fnValue.equalsIgnoreCase(UdpConstant.FNVALUE) || null == udpOperateEntity) {
            return null;
        } else {
            String addressDevice = map.get("addressDevice") + UdpConstant.AFTERADDRESS;
            String framesNo = map.get("framesNo");
            String rtFnValue = "00001000";
            String code = packMessageByWord(udpOperateEntity);
            String message = UdpConstant.TENGLIANHEAD + UdpConstant.LENGTH + UdpConstant.LENGTH + UdpConstant.SIXTH
                    + UdpConstant.CONTRCODE + addressDevice + UdpConstant.RTAFNVALUE + framesNo + rtFnValue + code;
            // 计算长度
            String messageWithLength = UdpTools.calculationLength(message);
            // 计算校验码
            String verification = UdpTools.calculationVerification(messageWithLength);
            return messageWithLength + verification + UdpConstant.LASTCODE;
        }
    }

    public static String packMessageByWord(UdpOperateEntity udpOperateEntity) {
        try {
            String actionName = udpOperateEntity.getActionName();
            if ("batteryAlarm".equals(actionName)) {
                // 电池电压阈值
                return "01" + udpOperateEntity.getValue() + "0000000000";
            } else if ("heart".equals(actionName)) {
                // 定时上报时间间隔
                String time = udpOperateEntity.getValue();
                if (time.length() == 3) {
                    time = "0" + time;
                } else if (time.length() == 2) {
                    time = "00" + time;
                }
                char time1 = time.charAt(0);
                char time2 = time.charAt(1);
                char time3 = time.charAt(2);
                char time4 = time.charAt(3);
                return "03" + time3 + "" + time4 + "" + time1 + "" + time2 + "00000000";
            } else if ("defense".equals(actionName)) {
                // 布防/撤防（消防闷盖）
                if (udpOperateEntity.getValue().equals("true")) {
                    return "04550000000000";
                } else {
                    return "04AA0000000000";
                }
            } else if ("ipPort".equals(actionName)) {
                // 前置机ip port（建议不要轻易修改，改的时候不要改错)
                String[] ipAddress = udpOperateEntity.getValue().split("\\.");
                String first = UdpTools.intToHex(ipAddress[0]);
                first = first.length() == 1 ? 0 + first : first;
                String second = UdpTools.intToHex(ipAddress[1]);
                second = second.length() == 1 ? 0 + second : second;
                String third = UdpTools.intToHex(ipAddress[2]);
                third = third.length() == 1 ? 0 + third : third;
                String fourth = UdpTools.intToHex(ipAddress[3]);
                fourth = fourth.length() == 1 ? 0 + fourth : fourth;
                String port = UdpTools.intToHex(ipAddress[4]);
                port = "0" + port;
                port = port.charAt(2) + "" + port.charAt(3) + "" + port.charAt(0) + port.charAt(1);
                return "05" + first + second + third + fourth + port;
            } else {
                throw new Exception("actionName is wrong,actionName=" + actionName);
            }
        } catch (Exception e) {
            log.info(e.toString());
        }
        return "";
    }
}
