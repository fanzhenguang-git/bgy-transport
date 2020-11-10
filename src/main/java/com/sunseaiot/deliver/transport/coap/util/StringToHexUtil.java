/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.util;

/**
 * ascii与hex转换工具类
 *
 * @author jyl
 * @date 2019-08-20
 */
public class StringToHexUtil {

    /**
     * 传入String 转成hex
     */
    public static String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString(aChar));
        }
        return hex.toString();
    }

    /**
     * 传入HEX转成String
     */
    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        int a = 2;
        for (int i = 0; i < hex.length() - 1; i += a) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }
}