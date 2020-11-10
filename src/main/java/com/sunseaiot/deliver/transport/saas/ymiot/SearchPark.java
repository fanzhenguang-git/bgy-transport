/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.ymiot;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 查询智慧停车开放平台接口
 * @author: xwb
 * @create: 2019/08/21 18:19
 */
@Slf4j
@Component
public class SearchPark {

    // saas平台服务地址
    private static final String HOST = "http://openapi.ymlot.cn";

    public static final String RESPONSE_ERROR_CODE = "0";

    public static final String RESPONSE_SUCCESS_CODE = "1";

    // APPSecret: 用于加密签名字符串和服务器端验证签名字符串的密钥
    private final String appSecret = "18aeb955b8e144028ff20b5183885bbe";

    // APPID: 用于标识用户，用户将APPID 放入访问请求，以便智慧停车平台识别访问者的身份
    private final String appId = "ymfe7b1fbfa4f488c8";

    public Map findParkInfo() {

        try {
            String url = "/Api/Inquire/GetParkInfo";
            Map<String, Object> param = new HashMap<>();
            param.put("parkKey", "1j4rdfrp");
            param.put("version", "v1.0");
            param.put("appid", appId);
            // 随机数生成
            param.put("rand", String.valueOf(SignTransfer.getDouble()));
            // 签名算法参数转换
            String signstr = SignTransfer.map2SignString(param, appSecret);
            signstr = SimpleMd5Util.md5(signstr);
            param.put("sign", signstr);
            // System.out.println(signstr);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(HOST + url, JSONObject.toJSONString(param));
            JSONObject OnoffObject = JSONObject.parseObject(strRespOnoff);
            System.out.println(OnoffObject.toString());
            // 暂时没有成功回调的返回值，所以无法解析返回给shadow
            if (SearchPark.RESPONSE_SUCCESS_CODE.equals(OnoffObject.getString("code"))) {
                log.info("http request is success!");
            } else {
                log.error("http request is unsuccess!");
            }

            return null;
        } catch (Exception e) {
            log.info("execute exception:={}", e.getMessage());
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }
}
