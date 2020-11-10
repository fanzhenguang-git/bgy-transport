/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import io.netty.buffer.ByteBuf;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * String 操作工具类
 *
 * @author: fanbaochun
 * @date: 2019/8/15
 */
public class StringUtil {
    // 判断日期
    public static boolean isDateString(String datevalue, String dateFormat) {
        if (StringUtils.isEmpty(datevalue)) {
            return false;
        }
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
            java.util.Date dd = fmt.parse(datevalue);
            if (datevalue.equals(fmt.format(dd))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // 判断整数（int）
    public static boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    // 判断浮点数（double和float）
    public static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?\\d*[.]\\d+$");
        return pattern.matcher(str).matches();
    }

    // ByteBuf转String
    public static String convertByteBufToString(ByteBuf buf) {
        String str;
        if (buf.hasArray()) {
            // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else {
            // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }
}
