/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录奇迹平台获取token
 *
 * @author hupan
 * @date 2019-08-14.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class QueryAccessToken {

    /**
     * @description: 取得授权令牌
     * @author: xjp
     * @param: [name, hostPort, appId, appSecret]
     * @return: java.lang.String
     * @date: 2019/8/15
     */
    public String queryAccessToken(String name, String hostPort, String appId, String appSecret) throws Exception {

        // 授权地址
        String authUrl = "/iot/auth/getAccessToken";
        Map<String, Object> authParams = new HashMap<>();
        authParams.put("appId", appId);
        authParams.put("appSecret", appSecret);
        // 发送http post请求
        String jsonResp = HttpUtil.httpPost(hostPort + authUrl, authParams);
        System.out.println(jsonResp);
        JSONObject body = JSONObject.parseObject(jsonResp);
        if (!"000".equals(body.getString("code"))) {
            log.error(name + "Method failed to obtain the authorization token！:{}", body.getString("msg"));
        }
        // 取得授权令牌
        return body.getJSONObject("body").getString("accessToken");
    }
}