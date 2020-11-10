/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.service;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.transport.udp.gd.bean.DeviceDataDTO;
import com.sunseaiot.deliver.transport.udp.gd.util.DeviceSerialIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.*;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

/**
 * Udp客户端Socket服务
 *
 * @author weishaopeng
 * @date 2019/12/25 9:12
 */
@Slf4j
@Component
public class UdpClientSocketService {

    @Value("${deliver.transport.gd.server-ip}")
    private String serverIp;

    @Value("${deliver.transport.gd.server-port}")
    private String port;

    @Value("${deliver.transport.gd.app-id}")
    private String appId;

    @Value("${deliver.transport.gd.login-name}")
    private String loginName;

    @Value("${deliver.transport.gd.login-pwd}")
    private String loginPwd;

    @Value("${deliver.transport.gd.login-type}")
    private String loginType;

    @Value("${deliver.transport.gd.token}")
    private String token;

    @Autowired
    private EcologyDeviceDataService ecologyDeviceDataService;

    // 设备透传过来的数据的16进制有效数字
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    // 登录状态
    private boolean loginSuccess;

    // 创建DatagramSocket对象
    private DatagramSocket socket;

    {
        try {
            socket = new DatagramSocket(4003);
            // 设置阻塞时长20秒
            // socket.setSoTimeout(1000 * 20);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录到LoRaWAN网关
     */
    void udp4cityAir() {

        // 1.定义服务器的地址、端口号、数据
        InetAddress address = null;
        try {
            address = InetAddress.getByName(serverIp);
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
        int port2 = Integer.parseInt(port);
        String loginJSONStr = "{'as':{'app_id':'" + appId + "','login_name':'" + loginName + "','login_pwd':'" + loginPwd + "','login_type':" + loginType + ",'token':" + token + "}}";
        byte[] loginData = loginJSONStr.getBytes();
        // 2.创建数据报，包含发送的数据信息
        DatagramPacket packet = new DatagramPacket(loginData, loginData.length, address, port2);
        // 3.创建DatagramSocket对象
        //  DatagramSocket socket = new DatagramSocket();
        // 4.向服务器端发送数据报
        try {
            socket.send(packet);
            loginSuccess = true;
        } catch (IOException e) {
            loginSuccess = false;
            log.error(e.getMessage());
        }
    }

    /**
     * 等待LoRa回复
     */
    void receiveData() {
        while (loginSuccess) {
            // 接收服务器响应的数据
            byte[] data3 = new byte[1024];
            DatagramPacket packet3 = new DatagramPacket(data3, data3.length);
            try {
                socket.receive(packet3);
            } catch (IOException e) {
                loginSuccess = false;
                break;
            }
            String reply3 = new String(data3, 0, packet3.getLength());
            parseLoraDeviceData(reply3);
        }
    }

    /**
     * 解析LoraWAN发送过来的设备数据
     *
     * @param reply3 Lora发送过来Json字符串
     */
    public void parseLoraDeviceData(String reply3) {
        log.info(reply3);
        JSONObject app = JSONObject.parseObject(reply3).getJSONObject("app");
        if (null != app) {
            log.info(app.toString());
            // 终端Eui
            String moteEui = app.getString("moteeui");
            // 设备本次透传过来的JSON字符串
            JSONObject userdata = app.getJSONObject("userdata");
            // payload: 设备本次透传的数据
            StringBuilder sb = new StringBuilder();
            sb.append(userdata.getString("payload"));
            int payloadLength = sb.toString().length();
            for (int i = 0; i < payloadLength % 4; i++) {
                sb.append("=");
            }
            // LoRa接收设备数据的网关,可能有多个网关收到该设备上传的数据,取第一个网关即可
            JSONObject gw = (JSONObject) app.getJSONArray("gwrx").get(0);
            // 网关透传过来的时间,也作为设备上传数据的时间
            String time = gw.getString("time");
            // 网关id,网关唯一标识
            String gwEui = gw.getString("eui");
            // base64解析payload成字节数组
            byte[] decoded = Base64.getMimeDecoder().decode(sb.toString());
            // 把payload字节数组解析成十六进制字符串
            String upData = toHex(decoded);
            // 截取payload前两位报文,这是设备的唯一地址标识
            String deviceSerial = upData.substring(0, 2);
            // 根据地址值获取产品类型名称
            Map deviceSerialIdMap = DeviceSerialIdUtil.getDeviceSerialIdMap();
            String productTypeValue = (String) deviceSerialIdMap.get(deviceSerial);
            // 如果找不到对应的产品
            if (StringUtils.isEmpty(productTypeValue)) {
                log.error("device Serial is empty: {}", deviceSerial);
                return;
            }
            // 设备透传的第一个属性数据名称
            String attributeName1 = null;
            // 设备透传的第二个属性数据名称,目前只有 温湿度,土壤盐,氨氮 三款产品有该属性数据
            String attributeName2 = null;
            // 数据报文的第一个16进制属性数据字符串
            String upValueStr;
            // 数据报文的第二个16进制属性数据字符串
            String upValueStr2;
            // 转换后的第一个数据报文浮点数据
            Double upValue = null;
            // 转换后的第二个数据报文浮点数据
            Double upValue2 = null;
            // 数据的小数点位数
            String dig;

            // 根据报文第一跟第二位分类产品
            switch (productTypeValue) {
                case "DB":
                    // E1地址: 噪音变送器
                    attributeName1 = "DB";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "CO":
                    // E2地址: CO变送器
                    attributeName1 = "CO";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "CO2":
                    // E3地址: CO2变送器
                    attributeName1 = "CO2";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "NO":
                    // E4地址: NO变送器
                    attributeName1 = "NO";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "NO2":
                    // (0-8)1地址: NO2变送器
                    attributeName1 = "NO2";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "SO2":
                    // (0-8)2地址: SO2变送器
                    attributeName1 = "SO2";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "O3":
                    // (0-8)4地址: O3变送器
                    attributeName1 = "O3";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "PM2.5/PM10":
                    // (0-8)5地址: PM2.5/PM10变送器
                    attributeName1 = "PM2.5/PM10";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "WSD":
                    // (0-8)6地址: 温湿度变送器
                    attributeName1 = "WD";
                    upValueStr = upData.substring(6, 10);
                    upValue = (parseHex4(upValueStr) / 100);
                    // 湿度
                    attributeName2 = "SD";
                    upValueStr2 = upData.substring(10, 14);
                    upValue2 = ((double) Long.parseLong(upValueStr2, 16) / 100);
                    break;
                case "FS":
                    // (0-8)7地址: 风速变送器
                    attributeName1 = "FS";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 100);
                    break;
                case "FX":
                    // (0-8)8地址: 风向变送器
                    attributeName1 = "FX";
                    upValueStr = upData.substring(6, 10);
                    upValue = ((double) Long.parseLong(upValueStr, 16));
                    break;
                case "DQY":
                    // (0-8)9地址: 大气压变送器
                    attributeName1 = "DQY";
                    upValueStr = upData.substring(6, 14);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / 10);
                    break;
                case "DO":
                    // (0-4)A地址: 浊度变送器
                    attributeName1 = "DO";
                    upValueStr = upData.substring(6, 10);
                    dig = upData.substring(10, 14);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / changeDigit(dig));
                    break;
                case "RJY":
                    // (0-4)B地址: 溶解氧
                    attributeName1 = "RJY";
                    upValueStr = upData.substring(6, 10);
                    dig = upData.substring(10, 14);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / changeDigit(dig));
                    break;
                case "PH":
                    // (0-4)C地址: PH值
                    attributeName1 = "PH";
                    upValueStr = upData.substring(6, 10);
                    dig = upData.substring(10, 14);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / changeDigit(dig));
                    break;
                case "SW":
                    // (0-4)D地址: 水温变送器
                    attributeName1 = "SW";
                    upValueStr = upData.substring(6, 10);
                    upValue = (((double) Long.parseLong(upValueStr, 16) / 10) - 30);
                    break;
                case "DDLTRY":
                    // (0-4)E地址: 电导率+土壤盐
                    attributeName1 = "DDL";
                    upValueStr = upData.substring(6, 10);
                    dig = upData.substring(10, 14);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / changeDigit(dig));
                    // 土壤盐 温度
                    attributeName2 = "TRYWD";
                    upValueStr2 = upData.substring(14, 18);
                    upValue2 = ((double) Long.parseLong(upValueStr2, 16) / changeDigit(upData.substring(18, 22)));
                    break;
                case "NH3":
                    // (0-4)F地址: 氨氮在线监测
                    attributeName1 = "NH3";
                    upValueStr = upData.substring(6, 10);
                    dig = upData.substring(10, 14);
                    upValue = ((double) Long.parseLong(upValueStr, 16) / changeDigit(dig));
                    // 氨氮值温度
                    attributeName2 = "NH3WD";
                    upValueStr2 = upData.substring(14, 18);
                    upValue2 = ((double) Long.parseLong(upValueStr2, 16) / changeDigit(upData.substring(18, 22)));
                    break;
                default:
                    break;
            }
            DeviceDataDTO deviceDataDTO = new DeviceDataDTO(time, upData, deviceSerial, moteEui, gwEui, attributeName1, attributeName2, upValue, upValue2);
            log.info(deviceDataDTO.toString());
            ecologyDeviceDataService.saveEcologyDeviceData(Collections.singletonList(deviceDataDTO));
        }
    }

    /**
     * 发送下行数据到LoRaWAN网关
     */
    public void downStreamUdp4cityAir(String downStreamJsonStr) throws IOException {

        // 1.定义服务器的地址、端口号、数据
        InetAddress address = null;
        try {
            address = InetAddress.getByName(serverIp);
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
        int port2 = Integer.parseInt(port);
        byte[] loginData = downStreamJsonStr.getBytes();
        // 2.创建数据报，包含发送的数据信息
        DatagramPacket packet = new DatagramPacket(loginData, loginData.length, address, port2);
        // 3.向服务器端发送数据报
        socket.send(packet);
    }

    /**
     * 小数位的个数
     *
     * @param s 小数位的个数
     * @return 返回小数位的个数
     */
    private static int changeDigit(String s) {
        int i = 0;
        if (!StringUtils.isEmpty(s)) {
            double d = Double.parseDouble(s);
            if (d == 0) {
                i = 1;
            } else if (d == 1) {
                i = 10;
            } else if (d == 2) {
                i = 100;
            } else if (d == 3) {
                i = 1000;
            } else if (d == 4) {
                i = 10000;
            }
        }
        return i;
    }

    /**
     * 把字节数组转换为16进制字符串
     *
     * @param data 需要转换的字节数组
     * @return 16进制字符串
     */
    private static String toHex(byte[] data) {
        final StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte aData : data) {
            sb.append(DIGITS[(aData >>> 4) & 0x0F]);
            sb.append(DIGITS[aData & 0x0F]);
        }
        return sb.toString();
    }

    /**
     * 十六进制转正负数 (2个字节的)
     *
     * @param num 转换的十六进制字符串
     * @return 十进制的浮点数
     */
    private static double parseHex4(String num) {
        if (num.length() != 4) {
            throw new NumberFormatException("Wrong length: " + num.length() + ", must be 4.");
        }
        int ret = Integer.parseInt(num, 16);
        ret = ((ret & 0x8000) > 0) ? (ret - 0x10000) : (ret);
        return (double) ret;
    }
}
