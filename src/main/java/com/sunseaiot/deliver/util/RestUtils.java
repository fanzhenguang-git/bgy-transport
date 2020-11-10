/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import com.ctg.ag.sdk.core.model.BaseApiResponse;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 返回参数封装工具类
 *
 * @Author: wanglong
 * @Date: 2020/1/10
 **/
public class RestUtils {
    private final static Logger logger = LoggerFactory.getLogger(RestUtils.class);

    /**
     * 封装参数
     **/
    public static Object wrap(BaseApiResponse response) throws UnsupportedEncodingException {
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            String bodyContent = new String(response.getBody(), "UTF-8");
            logger.info(bodyContent);
            return Result.ok(bodyContent);
        }
        return Result.error(response.getMessage());
    }

    /**
     * 封装参数
     **/
    public static <T> Result<T> wrap(BaseApiResponse response, Class<T> type) throws
            UnsupportedEncodingException {
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            String bodyContent = new String(response.getBody(), "UTF-8");
            Map map = new Gson().fromJson(bodyContent, Map.class);
            if (!(type.isAssignableFrom(Result.class)) && (map.containsKey("result"))) {
                Object result = map.get("result");
                String toJson = new Gson().toJson(result);
                System.out.println(toJson);
                T json = new Gson().fromJson(toJson, type);
                return Result.ok(json);
            }
            return Result.ok(bodyContent);
        }
        return Result.error(response.getMessage());
    }

    /**
     * 转byte[]
     **/
    public static byte[] convert2Bytes(Object object) throws UnsupportedEncodingException {
        String toJson = new Gson().toJson(object);
        return toJson.getBytes("UTF-8");
    }
}
