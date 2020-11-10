/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * udp工具类
 *
 * @author qixiaofei
 * @date 2019-09-23.
 */
@Slf4j
public class UdpTools {

    /**
     * 根据文档校验报文是否完整
     *
     * @param bytes UdpServer端接收到的报文
     * @return true 表示校验通过 false 表示校验失败
     */
    public static boolean tenglianVerification(byte[] bytes) {
        long result = 0L;
        for (int i = 6; i < bytes.length - 2; i++) {
            String s = byteToHex(bytes[i]);
            long dec_num = Long.parseLong(s, 16);
            result += dec_num;
        }
        String code = Integer.toHexString((int) result);
        String message = byteToHex(bytes[bytes.length - 2]);
        long l = Long.parseLong(message, 16);
        return code.substring(code.length() - 2).equalsIgnoreCase(Integer.toHexString((int) l));
    }

    /**
     * byte转16进制字符串
     *
     * @param b byte字节
     * @return 16进制字符串
     **/
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 16进制转换为ASCII
     *
     * @param hex 16进制字符串
     * @return ASCII码
     */
    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    /**
     * 通过报文获取设备的基本信息
     *
     * @param bytes 报文的byte数组
     * @return map里面包含的设备基本信息
     **/
    public static Map<String, String> getBasicInfoFromMessage(byte[] bytes) {
        Map<String, String> map = new HashMap<>();
        String contrChar = UdpTools.bytes2StrByIndex(bytes, 6);
        String addressPlatform =
                UdpTools.bytes2StrByIndex(bytes, 8) + UdpTools.bytes2StrByIndex(bytes, 7) + UdpTools.bytes2StrByIndex(bytes, 10) + UdpTools.bytes2StrByIndex(bytes, 9); //第8-12个字节地址域 其中8-11是通信地址转换后37991956
        String addressDevice =
                UdpTools.bytes2StrByIndex(bytes, 7) + UdpTools.bytes2StrByIndex(bytes, 8) + UdpTools.bytes2StrByIndex(bytes, 9) + UdpTools.bytes2StrByIndex(bytes, 10);
        String afnValue = UdpTools.bytes2StrByIndex(bytes, 12);
        String framesNo = UdpTools.bytes2StrByIndex(bytes, 13);
        String fnValue = UdpTools.bytes2StrByIndex(bytes, 14) + UdpTools.bytes2StrByIndex(bytes, 15) + UdpTools.bytes2StrByIndex(bytes, 16) + UdpTools.bytes2StrByIndex(bytes, 17);
        String iMei = "";
        map.put("contrChar", contrChar);
        map.put("addressPlatform", addressPlatform);
        map.put("afnValue", afnValue);
        map.put("fnValue", fnValue);
        map.put("framesNo", framesNo);
        map.put("addressDevice", addressDevice);
        return map;
    }

    /**
     * 根据腾联的协议计算长度
     *
     * @param message 生成的部分报文，用 0000 代替长度
     * @return 用计算出的长度替换 0000 后的报文
     */
    public static String calculationLength(String message) {
        String result = "000000000" + Integer.toBinaryString(19) + "10";
        String substring = result.substring(result.length() - 8);
        String s = binaryString2hexString(substring);
        String length = s.toUpperCase() + "00";
        return message.replaceFirst("00000000", length + length);
    }

    /**
     * 根据腾联的协议计算校验码
     *
     * @param message 不包含校验码的报文
     * @return 计算出的校验码
     */
    public static String calculationVerification(String message) {
        try {
            byte[] split = hexTobytes(message);
            long result = 0l;
            for (int i = 6; i < split.length; i++) {
                result += split[i];
            }
            String code = Integer.toHexString((int) result);
            return code.substring(code.length() - 2).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("calculationVerification Method",e);
            return "";
        }
    }

    /**
     * 根据数据数组下标获取16进制字符串
     */
    public static String bytes2StrByIndex(byte[] src, int i) {
        StringBuilder stringBuilder = new StringBuilder("");
        int v = src[i] & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }

    /**
     * 将报文转换为16进制的byte[]数组
     **/
    public static byte[] hexTobytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i = i + 2) {
            String subStr = hex.substring(i, i + 2);
            boolean flag = false;
            int intH = Integer.parseInt(subStr, 16);
            if (intH > 127) flag = true;
            if (intH == 128) {
                intH = -128;
            } else if (flag) {
                intH = 0 - (intH & 0x7F);
            }
            byte b = (byte) intH;
            bytes[i / 2] = b;
        }
        return bytes;
    }

    /**
     * 十六进制转byte[]
     *
     * @param inHex 16进制字符串
     * @return byte[]数组
     **/
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * 字符串转16进制byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 十进制转十六进制
     */
    public static String intToHex(String message) {
        int n = Integer.parseInt(message);
        StringBuffer s = new StringBuffer();
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            s = s.append(b[n % 16]);
            n = n / 16;
        }
        a = s.reverse().toString();
        return a;
    }

    /**
     * 2进制转16进制字符串
     */
    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 此方法是将收到的数组转换为字符串，为了方便debug，生产环境中无需使用
     */
    public static String receiveHexToString(byte[] bytes) {
        try {
            String str = bytes2Str(bytes);
            str = str.toLowerCase();
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("receiveHexToString Method 接收字节数据并转为16进制字符串异常",ex);
        }
        return null;
    }

    /**
     * 此方法是将收到的数组转换为字符串，为了方便debug，生产环境中无需使用
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
