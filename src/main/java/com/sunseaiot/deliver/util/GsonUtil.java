/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * JSON 操作工具类
 *
 * Created by wangyongjun on 2017/7/25
 */
public class GsonUtil {

    private static final JsonParser PARSER = new JsonParser();

    public static JsonElement fromJson(String json) {
        return PARSER.parse(json);
    }
}
