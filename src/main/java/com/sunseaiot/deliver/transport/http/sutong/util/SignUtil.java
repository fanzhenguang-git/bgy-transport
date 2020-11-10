/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.http.sutong.util;

import java.security.MessageDigest;
import java.util.TreeMap;

/**
 * 光感地磁签名验证
 *
 * @author jyl
 * @date 2019-09-06
 */
public class SignUtil {

    /**
     * 生成sign签名
     */
    public static String checkSign(TreeMap<String, String> treeMap) {

        StringBuilder sb = new StringBuilder();
        for (String value : treeMap.values()) {
            sb.append(value);
        }
        String sign;
        try {
            sign = Encrypt(sb.toString());
            return sign;
        } catch (Exception e) {
            e.getMessage();
        }
        return "";
    }

    /**
     * MD5签名
     */
    private static String Encrypt(String strSrc) throws Exception {
        byte[] bt = strSrc.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bt);
        return bytes2Hex(md.digest());
    }

    /**
     * bytes转Hex
     */
    private static String bytes2Hex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        for (byte bt : bts) {
            String tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }
}