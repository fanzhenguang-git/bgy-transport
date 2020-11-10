/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.util;

/**
 * CRC-16/CCITT-FALSE算法校验
 *
 * @author jyl
 * @date 2019-08-20
 **/
public class Crc16Utils {

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || "".equals(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static int crc16(byte[] bytes, int len) {
        int crc = 0xFFFF;
        for (int j = 0; j < len; j++) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            // byte to int, trunc sign
            crc ^= (bytes[j] & 0xff);
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;
    }
}
