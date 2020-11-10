/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji;

import com.alibaba.fastjson.JSONArray;
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
 * 灯控模块
 *
 * @author: XJP
 * @create: 2019/08/15 16:57
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class LampControl {

    // 打开
    public static final String LAMP_ON = "open";
    // 关闭
    public static final String LAMP_OFF = "close";

    @Autowired
    private QueryAccessToken queryAccessToken;

    /**
     * @description: 开关灯（非策略下调用）
     * @author: XJP
     * @param: [devSn, onOff]
     * @return: map
     * @date: 2019/8/15 19:06
     */
    public Map onOffLamp(String devSn, boolean onOff, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("onOffLamp", hostPort, appId, appSecret);
            // 灯控开关接口
            String url = "/iot/lampControl/onOff";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("onOff", onOff);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 设置亮度（非策略下调用）
     * @author: XJP
     * @param: [devSn, brightness]
     * @return: java.util.Map
     * @date: 2019/8/15 19:27
     */
    public Map setBrightness(String devSn, int brightness, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("setBrightness", hostPort, appId, appSecret);
            String url = "/iot/lampControl/setBrightness";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("brightness", brightness);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 策略下发
     * @author: XJP
     * @param: [devSn, strategyId, rules]
     * @return: java.util.Map
     * @date: 2019/8/16 10:09
     */
    public Map addStrategy(String devSn, String strategyId, JSONArray rules, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("addStrategy", hostPort, appId, appSecret);
            String url = "/iot/lampControl/addStrategy";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("strategyId", strategyId);
            param.put("accessToken", accessToken);
            param.put("rules", rules);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 删除策略
     * @author: XJP
     * @param: [devSn, strategyId]
     * @return: java.util.Map
     * @date: 2019/8/16 10:21
     */
    public Map removeStrategy(String devSn, String strategyId, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("removeStrategy", hostPort, appId, appSecret);
            String url = "/iot/lampControl/removeStrategy";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("strategyId", strategyId);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 控制开关（只有在策略下才能调用）
     * @author: XJP
     * @param: [devSn, onOff, time]
     * @return: java.util.Map
     * @date: 2019/8/16 14:18
     */
    public Map handOnOff(String devSn, boolean onOff, long time, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("handOnOff", hostPort, appId, appSecret);
            String url = "/iot/lampControl/handOnOff";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("onOff", onOff);
            // 手动控制时长
            param.put("time", time);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 调节亮度（只有在策略下才能调用）
     * @author: XJP
     * @param: [devSn, onOff, time]
     * @return: java.util.Map
     * @date: 2019/8/16 14:18
     */
    public Map handSetBrightness(String devSn, long brightness, long time, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("handSetBrightness", hostPort, appId, appSecret);
            String url = "/iot/lampControl/handSetBrightness";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("brightness", brightness);
            // 手动控制时长
            param.put("time", time);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 设置灯控模式（只有在策略下才能调用）
     * @author: XJP
     * @param: [devSn, onOff, time]
     * @return: java.util.Map
     * @date: 2019/8/16 14:18
     */
    public Map enableTimePolicy(String devSn, boolean enable, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("enableTimePolicy", hostPort, appId, appSecret);
            String url = "/iot/lampControl/enableTimePolicy";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("enable", enable);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }
}
