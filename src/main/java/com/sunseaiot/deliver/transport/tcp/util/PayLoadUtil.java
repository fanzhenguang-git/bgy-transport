/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.util;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dto.TypeValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * tcp相关设备数据处理 通用方法提取类
 *
 * @author fanbaochun
 */
@Slf4j
public class PayLoadUtil {

    private static String hex8 = "%02x";
    private static String hex16 = "%04x";
    private static String hex32 = "%08x";

    /**
     * 重组字符串，length必须是偶数 如0103020D01E00141016700030000  重组成01,03,02,0D,01,E0...
     *
     * @param str
     */
    public static String restructureStr(String str) {
        if (str.length() % 2 != 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i += 2) {
            String output = str.substring(i, (i + 2));
            if (i == str.length() - 2) {
                stringBuilder.append(output);
            } else {
                stringBuilder.append(output).append(",");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 比较两个数字大小
     * @param val1
     * @param val2
     */
    public static boolean compareNumberSize(int val1, int val2) {
        if (val1 < val2) {
            return true;
        }
        return false;
    }

    /**
     * 获取数组中指定位置的字节组
     */
    public static byte[] getBytes(int start, int end, byte[] array) throws Exception {
        if (start > array.length || end > array.length) {
            log.warn("Subscript Error,start:{},end:{},array length:{} ", start, end, array.length);
        }
        int length = end - start;
        int index = 0;
        byte[] bytes = new byte[length];
        for (int i = start; i < array.length; i++) {
            if (i == end) {
                break;
            }
            bytes[index] = array[i];
            index++;
        }
        return bytes;
    }

    /**
     * 获取数组中指定位置的字节 返回16进制字符串
     */
    public static String getBytesStr(int start, int end, byte[] bytes) throws Exception {
        if (start > bytes.length || end > bytes.length) {
            log.warn("Index Error,start:{},end:{},array length:{} ", start, end, bytes.length);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = start; i < bytes.length; i++) {
            if (i == end) {
                break;
            }
            String str = Integer.toHexString(bytes[i] & 0xff);
            if (str.length() == 1) {
                stringBuilder.append(0);
            }
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    /**
     * @param start 起始位置
     * @param end   结束位置
     * @param array 数组
     * @Description 计算校验和
     */
    public static int getCheckCount(int start, int end, String[] array) {
        if (start > array.length || end > array.length) {
            log.warn("Subscript Error,start:{},end:{},array length:{} ", start, end, array.length);
        }
        int count = 0;
        for (int i = start; i < array.length; i++) {
            if (i == end) {
                break;
            }
            count += Long.parseLong(array[i], 16);
        }
        return count;
    }

    /**
     * @param start 起始
     * @param end   结束
     * @param array 字节组
     * @Description 计算校验和，字节组形式
     */
    public static int getCheckCount1(int start, int end, byte[] array) {
        if (null == array || array.length < 0) {
            return 0;
        }
        int count = 0;

        for (int i = start; i < array.length; i++) {
            if (i == end) {
                break;
            }
            count += (array[i] & 0xff);
        }
        return count;
    }

    /**
     * @param val
     * @Description int转byte字节
     */
    public static byte[] intToByte(int val) {
        byte[] b = new byte[4];
        b[0] = (byte) (val & 0xff);
        b[1] = (byte) ((val >> 8) & 0xff);
        b[2] = (byte) ((val >> 16) & 0xff);
        b[3] = (byte) ((val >> 24) & 0xff);
        return b;
    }

    /**
     * @param str
     * @Description 16进制转换成为string类型字符串
     */
    public static String hexStringToString(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        str = str.replace(" ", "");
        byte[] baKeyword = new byte[str.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            str = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return str;
    }

    /**
     * @param s
     * @Description 字符串转16进制
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * @param b
     * @Description 10进制转16进制  1个字节
     */
    public static String numToHex8(int b) {
        return String.format(hex8, b);
    }

    /**
     * @param b
     * @Description 10进制转16进制  2个字节
     */
    public static String numToHex16(int b) {
        return String.format(hex16, b);
    }

    /**
     * @param b
     * @Description 10进制转16进制  4个字节
     */
    public static String numToHex32(int b) {
        return String.format(hex32, b);
    }


    /**
     * @param chars 字节长度
     * @param value 字符串
     * @Description string类型转16进制，字节不够补0
     */
    public static String getHexString(int chars, String value) {
        String str = PayLoadUtil.strTo16(value);
        if (str.length() < chars) {
            while (str.length() < chars) {
                str += "0";
            }
        }
        return str;
    }

    /**
     * @param map TypeValue map
     * @Description valueMap to json
     */
    public static String map2JsonStr(Map<String, TypeValue> map) {
        JSONObject jsonObject = new JSONObject();
        map.forEach((k, v) -> {
            switch (v.getValueCase()) {
                case BOOLVALUE:
                    jsonObject.put(k, v.getBoolValue());
                    break;
                case INTVALUE:
                    jsonObject.put(k, v.getIntValue());
                    break;
                case DOUBLEVALUE:
                    jsonObject.put(k, v.getDoubleValue());
                    break;
                case STRINGVALUE:
                    jsonObject.put(k, v.getStringValue());
                    break;
                default:
                    log.warn("unknow type");
                    return;
            }
        });
        return jsonObject.toJSONString();
    }

    /**
     * @param hexString
     * @Description 16进制字符串转byte
     */
    public static byte[] toByteArray(String hexString) {
        if (StringUtils.isEmpty(hexString)) {
            throw new IllegalArgumentException("this hexString must not be empty");
        }

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * @param src
     * @Description byte字节转字符串
     */
    public static String bytes2Str(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
