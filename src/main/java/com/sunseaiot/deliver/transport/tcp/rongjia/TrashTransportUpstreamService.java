/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.rongjia;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.constant.TcpConstant;
import com.sunseaiot.deliver.transport.tcp.entity.TcpDeviceCacheEntity;
import com.sunseaiot.deliver.transport.tcp.service.impl.TcpDeviceCacheServiceImpl;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import com.sunseaiot.deliver.transport.udp.redis.impl.UdpOperateCacheDaoImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * RS垃圾桶 --上行数据处理类
 *
 * @author fanbaochun
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.tcp.enable", havingValue = "true")
public class TrashTransportUpstreamService {

    @Autowired
    private UpstreamService upstreamService;

    @Autowired
    private TcpDeviceCacheServiceImpl tcpDeviceCacheService;

    @Autowired
    private TrashTransportDownService trashTransportDownService;

    @Autowired
    private UdpOperateCacheDaoImpl udpOperateCacheDaoImpl;

    /**
     * 融家垃圾桶数据处理
     *
     * @param bytes 上行字节组
     * @param ctx   通道
     * @return 应答 字节组
     */
    public byte[] payLoadHandler(byte[] bytes, ChannelHandlerContext ctx) throws Exception {
        // 功能码
        byte code = bytes[3];
        byte[] back = null;
        switch (code) {
            case TcpConstant.RJ_BYTE_TIMING_CODE:
                back = timingPk(bytes);
                break;
            case TcpConstant.RJ_BYTE_REAL_TIME_CODE:
                realTimePk(bytes, ctx);
                break;
            case TcpConstant.RJ_BYTE_CARD_NUMBER_CODE:
                back = cardNumberPk(bytes);
                break;
            default:
                return back;
        }
        return back;
    }

    /**
     * @param bytes 报文
     * @Description 对时包
     */
    private byte[] timingPk(byte[] bytes) throws Exception {
        // 校验和
        byte checkCount = (byte) (bytes[10] & 0xff);
        // 计算校验和
        int countNew = PayLoadUtil.getCheckCount1(2, 10, bytes);
        // to byte
        int countToByte = PayLoadUtil.intToByte(countNew)[0];

        if (checkCount == countToByte) {
            return timingBack();
        }
        return null;
    }

    /**
     * @Description 对时包 -应答报文
     */
    private byte[] timingBack() {
        byte[] back = new byte[14];
        // 帧头
        back[0] = (byte) 0xaa;
        back[1] = 0x55;
        // 帧类型
        back[2] = (byte) 0xff;
        // 功能码
        back[3] = 0x04;
        // 获取系统当前时间 年 月 日 时 分 秒
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = (now.get(Calendar.MONTH) + 1);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        // 年份转byte 年份占2个字节
        byte[] years = PayLoadUtil.intToByte(year);
        back[4] = years[1];
        back[5] = years[0];
        // 月
        back[6] = PayLoadUtil.intToByte(month)[0];
        // 日
        back[7] = PayLoadUtil.intToByte(day)[0];
        // 时
        back[8] = PayLoadUtil.intToByte(hour)[0];
        // 分
        back[9] = PayLoadUtil.intToByte(minute)[0];
        // 秒
        back[10] = PayLoadUtil.intToByte(second)[0];
        // 计算校验和
        int checkNew = PayLoadUtil.getCheckCount1(2, 10, back);
        // 校验和
        back[11] = PayLoadUtil.intToByte(checkNew)[0];
        // 帧尾
        back[12] = (byte) 0xbb;
        back[13] = 0x66;
        return back;
    }

    /**
     * @param bytes 设备卡号上报字节组
     * @Description 卡号包
     */
    private byte[] cardNumberPk(byte[] bytes) throws Exception {
        // 设备序列号
        byte[] serialNumberBytes = PayLoadUtil.getBytes(4, 10, bytes);
        // 卡号 16进制度
        String cardNumber = PayLoadUtil.getBytesStr(10, 30, bytes);
        byte checkCount = bytes[30];
        int checkCountNew = PayLoadUtil.getCheckCount1(2, 30, bytes);
        int c = PayLoadUtil.intToByte(checkCountNew)[0];
        if (checkCount == c) {
            return cardMessageBack(serialNumberBytes);
        }
        return null;
    }

    /**
     * @param serialNumberBytes 设备序列号字节组
     * @Description 卡号包应答
     */
    private byte[] cardMessageBack(byte[] serialNumberBytes) {
        byte[] bytes = new byte[14];
        // 帧头
        bytes[0] = (byte) 0xaa;
        bytes[1] = 0x55;
        // 帧类型
        bytes[2] = (byte) 0xfe;
        // 功能码
        bytes[3] = 0x06;
        int index = bytes.length;
        for (byte b : serialNumberBytes) {
            bytes[index] = b;
            index++;
        }
        bytes[10] = 0x01;
        // 计算校验和
        int checkCountNew = PayLoadUtil.getCheckCount1(2, 11, bytes);
        // 校验和
        bytes[11] = PayLoadUtil.intToByte(checkCountNew)[0];
        // 帧尾
        bytes[12] = (byte) 0xbb;
        bytes[13] = (byte) 0x66;
        return bytes;
    }

    /**
     * @param bytes 字节组
     * @param ctx   channel通道
     * @Description 实时包 设备数据上报
     */
    private void realTimePk(byte[] bytes, ChannelHandlerContext ctx) throws Exception {
        // 设备序列号
        String serialNumber = PayLoadUtil.getBytesStr(4, 10, bytes);
        // 网络质量
        String networkQuality = PayLoadUtil.getBytesStr(10, 11, bytes);
        // 纬度
        String latitude = PayLoadUtil.getBytesStr(11, 22, bytes);
        // 经度
        String longitude = PayLoadUtil.getBytesStr(22, 34, bytes);
        // 16进制转字符 纬度
        Double latitudeValue = Double.valueOf(PayLoadUtil.hexStringToString(latitude));
        // 经度
        Double longitudeValue = Double.valueOf(PayLoadUtil.hexStringToString(longitude));
        // 卫星数量
        String satellitesNumber = PayLoadUtil.getBytesStr(34, 35, bytes);
        // 电池电压
        String batteryVoltage = PayLoadUtil.getBytesStr(35, 36, bytes);
        Double batteryVoltageValue = Double.valueOf(Long.parseLong(batteryVoltage, 16)) / 10;
        // 充电状态 0 未充电 、1 正在充电
        String chargingStatus = PayLoadUtil.getBytesStr(36, 37, bytes);
        // 门状态 0 关 、1开
        String doorStatus = PayLoadUtil.getBytesStr(37, 38, bytes);
        // 当日开门次数
        String openDoorsNumber = PayLoadUtil.getBytesStr(38, 40, bytes);
        // 垃圾桶状态
        String trashStatus = PayLoadUtil.getBytesStr(40, 41, bytes);
        // 垃圾桶容量
        String trashCapacity = PayLoadUtil.getBytesStr(41, 42, bytes);
        // 当日清桶次数
        String trashClearNumber = PayLoadUtil.getBytesStr(42, 44, bytes);
        // 故障代码
        String faultCode = PayLoadUtil.getBytesStr(44, 46, bytes);
        // 故障代码原始数据转2进制
        String faultBinaryString = Integer.toBinaryString(Integer.parseInt(faultCode, 16)).toString();
        // 故障代码
        String faultBinaryCode = faultBinaryString.charAt(0) + "";
        if (faultBinaryString.length() >= 4) {
            faultBinaryCode += "," + faultBinaryString.charAt(3);
        }
        // 当日压缩次数
        String compressNumber = PayLoadUtil.getBytesStr(46, 48, bytes);
        // 校验和
        byte checkCount = bytes[48];
        // 计算校验和
        int count = PayLoadUtil.getCheckCount1(2, 48, bytes);
        int countToByte = PayLoadUtil.intToByte(count)[0];
        if (checkCount != countToByte) {
            log.debug("Check failed check1 {},check2 {}", checkCount, countToByte);
            return;
        }
        // 封装map
        Map<String, TypeValue> attributeValueMap = new HashMap<String, TypeValue>();
        attributeValueMap.put("networkQuality", new TypeValue().setIntValue(Integer.parseInt(networkQuality, 16)));
        attributeValueMap.put("latitudeValue", new TypeValue().setDoubleValue(latitudeValue));
        attributeValueMap.put("longitudeValue", new TypeValue().setDoubleValue(longitudeValue));
        attributeValueMap.put("satellitesNumber", new TypeValue().setIntValue(Integer.parseInt(satellitesNumber, 16)));
        attributeValueMap.put("batteryVoltage", new TypeValue().setDoubleValue(batteryVoltageValue));
        attributeValueMap.put("chargingStatus", new TypeValue().setIntValue(Integer.parseInt(chargingStatus, 16)));
        attributeValueMap.put("doorStatus", new TypeValue().setIntValue(Integer.parseInt(doorStatus, 16)));
        attributeValueMap.put("openDoorsNumber", new TypeValue().setIntValue(Integer.parseInt(openDoorsNumber, 16)));
        attributeValueMap.put("trashStatus", new TypeValue().setIntValue(Integer.parseInt(trashStatus, 16)));
        attributeValueMap.put("trashCapacity", new TypeValue().setIntValue(Integer.valueOf(trashCapacity, 16)));
        attributeValueMap.put("trashClearNumber", new TypeValue().setIntValue(Integer.parseInt(trashClearNumber, 16)));
        attributeValueMap.put("faultBinaryCode", new TypeValue().setStringValue(faultBinaryCode));
        attributeValueMap.put("compressNumber", new TypeValue().setIntValue(Integer.parseInt(compressNumber, 16)));
        attributeValueMap.put("imei", new TypeValue().setStringValue(serialNumber));
        //String jsonStr = PayLoadUtil.map2JsonStr(attributeValueMap);
        // 获取设备id
        DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth("", serialNumber, "");
        String deviceId = deviceBaseInfo == null ? "" : deviceBaseInfo.getDeviceId();

        //告警联动数据payload添加deviceId
        Map<String, TypeValue> tempMap = new HashMap<>();
        tempMap.putAll(attributeValueMap);
        tempMap.put("deviceId", new TypeValue().setStringValue(deviceId));
        String jsonStr = PayLoadUtil.map2JsonStr(tempMap);
        // 上传到影子服务
        upstreamService.postOriginData(deviceId, jsonStr);
        upstreamService.postDeviceData(deviceId, attributeValueMap);
        // 下发指令
        onAction(ctx, serialNumber);
    }


    /**
     * @description: 根据电压-电池容量对照表 获取电池容量
     * @author: xwb
     * @param: [batteryVoltageValue]
     * @return: int
     * @date: 2019/10/22 10:57
     */
    public int convertBatteryVoltageValue(double batteryVoltageValue) {
        int batteryCapacity = 0;
        if (batteryVoltageValue > 10.50 && batteryVoltageValue <= 11.31) {
            batteryCapacity = 10;
        } else if (batteryVoltageValue > 11.31 && batteryVoltageValue <= 11.58) {
            batteryCapacity = 20;
        } else if (batteryVoltageValue > 11.58 && batteryVoltageValue <= 11.75) {
            batteryCapacity = 30;
        } else if (batteryVoltageValue > 11.75 && batteryVoltageValue <= 11.90) {
            batteryCapacity = 40;
        } else if (batteryVoltageValue > 11.90 && batteryVoltageValue <= 12.06) {
            batteryCapacity = 50;
        } else if (batteryVoltageValue > 12.06 && batteryVoltageValue <= 12.20) {
            batteryCapacity = 60;
        } else if (batteryVoltageValue > 12.20 && batteryVoltageValue <= 12.32) {
            batteryCapacity = 70;
        } else if (batteryVoltageValue > 12.32 && batteryVoltageValue <= 12.42) {
            batteryCapacity = 80;
        } else if (batteryVoltageValue > 12.42 && batteryVoltageValue <= 12.50) {
            batteryCapacity = 90;
        } else if (batteryVoltageValue > 12.50) {
            batteryCapacity = 100;
        }
        return batteryCapacity;
    }

    /**
     * 动作下发
     *
     * @param ctx  当前通道
     * @param imei 设备序列号
     */
    void onAction(ChannelHandlerContext ctx, String imei) {
        // 重启
        TcpDeviceCacheEntity tcpDeviceCacheEntity = tcpDeviceCacheService.findLast(imei, TcpConstant.RJ_RESTART);
        // 更换ip
        TcpDeviceCacheEntity tcpDeviceCacheEntity1 = tcpDeviceCacheService.findLast(imei,
                TcpConstant.RJ_REPLACEMENT_IP);

        if (null != tcpDeviceCacheEntity) {
            byte[] bytes = restart();
            writeAndFlush(ctx, imei, bytes);
            log.info("Trash Restart success ..");
            // 从redis中移除
            tcpDeviceCacheService.delete(imei, TcpConstant.RJ_RESTART);
        }
        if (null != tcpDeviceCacheEntity1) {
            byte[] bytes = replaceIp(imei, tcpDeviceCacheEntity1.getTypeValueObj());
            writeAndFlush(ctx, imei, bytes);
            log.info("Trash replace ip success ..");
            // 从redis中移除
            tcpDeviceCacheService.delete(imei, TcpConstant.RJ_REPLACEMENT_IP);
        }
    }

    /**
     * 重启
     */
    private byte[] restart() {
        log.info("Trash Restart ..");
        // 重启指令
        byte[] bytes = new byte[]{(byte) 0xaa, 0x55, (byte) 0xfe, 0x01, (byte) 0xff, (byte) 0xbb, 0x66};
        return bytes;
    }

    /**
     * 更换ip
     *
     * @param imei       设备序列号
     * @param jsonObject
     */
    private byte[] replaceIp(String imei, JSONObject jsonObject) {
        // 帧头
        String head = "aa55";
        // 帧类型
        String type = "81";
        // 功能码
        String code = "81";
        // 帧尾
        String tail = "bb66";
        // 16进制ip
        int ipChars = 0;
        String hexStringIp = PayLoadUtil.strTo16(jsonObject.getString("ip"));
        ipChars = 2 * 30;
        if (hexStringIp.length() < ipChars) {
            while (hexStringIp.length() < ipChars) {
                hexStringIp += "0";
            }
        }
        // 16进制端口
        String hexStringPort = String.valueOf(jsonObject.getIntValue("port"));
        hexStringPort = PayLoadUtil.getHexString(2 * 6, hexStringPort);
        // 16进制上传周期
        String hexStringCycle = PayLoadUtil.numToHex16(jsonObject.getIntValue("cycle"));
        // 生成报文
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(head).append(type)
                .append(code)
                .append(hexStringIp)
                .append(hexStringPort)
                .append(hexStringCycle);
        String restructureStr = PayLoadUtil.restructureStr(stringBuilder.toString());
        String[] array = restructureStr.split(",");
        int checkCount = PayLoadUtil.getCheckCount(2, 42, array);
        // 校验和转成16进制
        String hexString = Integer.toHexString(checkCount);
        // 取后两位
        String checkCode = hexString.substring(hexString.length() - 2, hexString.length());
        stringBuilder.append(checkCode).append(tail);
        log.info("Trash replace IP，message:{}", stringBuilder.toString());
        byte[] bytes = PayLoadUtil.toByteArray(stringBuilder.toString());
        return bytes;
    }

    private void writeAndFlush(ChannelHandlerContext ctx, String imei, byte[] bytes) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        log.debug("Get Channel {}", ctx.channel().remoteAddress());
        ctx.writeAndFlush(byteBuf);
    }
}
