/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import com.sunseaiot.deliver.dto.TypeValue;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TypeValue工具类
 *
 * @Author: wanglong
 * @Date: 2019/12/31
 **/
public class TypeValueUtils {

    /**
     * date format
     */
    private static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);

    /**
     * 判断value的类型，封装成TypeValue
     *
     * @param value
     * @param isObj
     * @return TypeValue
     */
    public static TypeValue getAttributeValue(String value, boolean isObj) {

        TypeValue attributeValue = new TypeValue();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (isObj) {
            attributeValue.setObjectValue(value);
        } else if (StringUtil.isDateString(value, DATE_FORMAT)) {
            attributeValue.setStringValue(value);
        } else if (StringUtil.isDouble(value)) {
            attributeValue.setDoubleValue(Double.valueOf(value));
        } else if (StringUtil.isInteger(value)) {
            attributeValue.setIntValue(Integer.valueOf(value));
        } else if ("true".equals(value) || "false".equals(value)) {
            boolean val = "true".equals(value);
            attributeValue.setBoolValue(val);
        } else {
            attributeValue.setStringValue(value);
        }
        return attributeValue;
    }

    /**
     * 时间戳日期转换
     */
    public static TypeValue getDateFormatAttributeValue(Long date, boolean isObj) {
        return getAttributeValue(fmt.format(new Date(date)), isObj);
    }
}
