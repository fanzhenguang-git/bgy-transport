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
 * 防盗锁模块
 *
 * @author: XJP
 * @create: 2019/08/16 16:31
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class AntiTheftLock {

    @Autowired
    private QueryAccessToken queryAccessToken;

    /**
     * @description: 开锁
     * @author: XJP
     * @param: [devSn]
     * @return: java.util.Map
     * @date: 2019/8/16 16:37
     */
    public Map open(String devSn, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("open", hostPort, appId, appSecret);
            String url = "/iot/safeLock/open";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
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
