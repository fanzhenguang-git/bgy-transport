/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.ymiot;

import com.alibaba.fastjson.JSONObject;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @description: 签名算法
 * @author: xwb
 * @create: 2019/08/21 15:28
 */
public class SignTransfer {


    /**
     * @description: 讲参数map中的key按签名算法，拼接执行MD5之前的字符串
     * @author: xwb
     * @param: [pmap]
     * @return: java.lang.String
     * @date: 2019/8/21 17:44
     */
    public static String map2SignString(Map<String, Object> pmap, String appSecret) {
        if (pmap == null || appSecret == null) {
            return null;
        }
        // 按Key进行字母大小排序
        Map<String, Object> sortedMap = sortMapByKey(pmap);
        StringBuffer sbf = new StringBuffer();
        Set<Map.Entry<String, Object>> entries = sortedMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            sbf.append(entry.getKey());
            sbf.append("=");
            sbf.append(String.valueOf(entry.getValue()));
            sbf.append("&");
        }
        sbf.append(appSecret);
        String signTemp = sbf.toString();
        return signTemp;
    }

    /**
     * 让 Map按key进行排序
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // 从小到大排序
                return o1.compareTo(o2);
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 随机生成指定精确度的小数 :请求接口时随机产生的小数，与上次请求不能重复，否则认为是非法请求，如9.949955201
     *
     * @return
     */
    public static double getDouble() {
        Random rand = new Random();
        DecimalFormat df = new DecimalFormat("#.000000");
        int a = (int) (Math.random() * 2 + 1);
        // int aa=(int)(Math.pow(-1, a));
        return Double.valueOf(df.format(rand.nextDouble() * 10 * a));
    }


    public static void main(String[] args) {
        HashMap<String, Object> tempmap = new HashMap<>();
        tempmap.put("f", "li");
        tempmap.put("d", "zs");
        tempmap.put("a", "张三");
        tempmap.put("c", "niuer");
        tempmap.put("b", "wan");

        String appSecret = "644e7b96967640bfb1bf70ff3f17afcd";
        String signstr = SignTransfer.map2SignString(tempmap, appSecret);
        System.out.println(signstr);

        signstr = SimpleMd5Util.md5(signstr);
        System.out.println(signstr);

        tempmap.put("sign", signstr);
        signstr = JSONObject.toJSONString(tempmap);
        System.out.println(signstr);

        System.out.println(getDouble());
    }
}