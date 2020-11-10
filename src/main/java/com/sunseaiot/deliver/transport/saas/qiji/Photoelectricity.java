/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 光电盒模块
 *
 * @author: XJP
 * @create: 2019/08/16 16:36
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class Photoelectricity {

    private static final String HOST = "http://120.79.163.145:81";

    @Autowired
    private QueryAccessToken queryAccessToken;

    /**
     * @description: 控制单元开关
     * @author: XJP
     * @param: [devSn, outputs, onOff]
     * @return: java.util.Map
     * @date: 2019/8/16 16:40
     */
    public Map onOff(String devSn, int[] outputs, boolean onOff, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("onOff", hostPort, appId, appSecret);
            String url = "/iot/pdu/onOff";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            // 插口编号集合，从0开始
            param.put("outputs", outputs);
            param.put("onOff", onOff);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                log.error("The control command failed to issue!");
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 电能清零
     * @author: XJP
     * @param: [devSn, outputs]
     * @return: java.util.Map
     * @date: 2019/8/16 16:43
     */
    public Map clearEnergy(String devSn, int[] outputs, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("clearEnergy", hostPort, appId, appSecret);
            String url = "/iot/pdu/clearEnergy";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            // 插口编号集合，从0开始
            param.put("outputs", outputs);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                log.error("The control command failed to issue!");
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }
}
