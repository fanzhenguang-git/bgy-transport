/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.http.sutong.service;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.http.sutong.util.SignUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 苏通地磁实现类
 *
 * @author jyl
 * @date 2019-09-06
 */
@Service
public class GeomagnetismServiceImpl implements GeomagnetismService {

    private final UpstreamService upstreamService;

    public GeomagnetismServiceImpl(UpstreamService upstreamService) {
        this.upstreamService = upstreamService;
    }

    @Override
    public String upPlayLoad(String str) {

        TreeMap<String, String> map = new TreeMap<>();
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONObject js = new JSONObject();
        // 获取code值，用于验证签名
        map.put("code", jsonObject.getString("code"));
        // 获取key值，用于验证签名
        map.put("key", jsonObject.getString("key"));
        // 获取user值，用于验证签名
        map.put("user", jsonObject.getString("user"));
        // 获取pass值，用于验证签名
        map.put("pass", jsonObject.getString("pass"));
        // 获取stamp值，用于验证签名
        map.put("stamp", jsonObject.getString("stamp"));
        // 验证签名
        String sign = SignUtil.checkSign(map);
        Map<String, TypeValue> attributes = new HashMap<>();
        // 获取产品参数
        String data = jsonObject.getString("data");
        String d = data.substring(1, data.length() - 1);
        JSONObject b = JSONObject.parseObject(d);
        // 验证签名，签名正确解析数据
        if (sign.equals(jsonObject.getString("sign"))) {
            // 停车场号
            attributes.put("r", new TypeValue().setIntValue(Integer.parseInt(b.getString("r"))));
            // 车位号
            attributes.put("i", new TypeValue().setStringValue(b.getString("i")));
            // 是否停车
            attributes.put("c", new TypeValue().setStringValue(b.getString("c")));
            // 设备IMEI号
            String deviceName = b.getString("m");
            // 添加IMEI
            attributes.put("m", new TypeValue().setStringValue(deviceName));
            // 获取时间
            String time = b.getString("t");
            long l = Long.parseLong(time, 10);
            String date = new Timestamp(l).toString();
            // 添加时间
            attributes.put("t", new TypeValue().setStringValue(date));
            // 设备电压
            attributes.put("v", new TypeValue().setDoubleValue(Double.parseDouble(b.getString("v"))));
            // 信号强度
            attributes.put("s", new TypeValue().setDoubleValue(Double.parseDouble(b.getString("s"))));
            // 信噪比
            attributes.put("z", new TypeValue().setDoubleValue(Double.parseDouble(b.getString("z"))));
            // 是否在线
            attributes.put("o", new TypeValue().setStringValue(b.getString("o")));
            // 获取uuid
            String uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
            // 上传数据
            upstreamService.postDeviceData(uuid, attributes);
            //添加返回数据
            js.put("code", jsonObject.getString("code"));
            js.put("key", jsonObject.getString("key"));
            js.put("err", "0");
            return js.toJSONString();
        }
        return "";
    }
}