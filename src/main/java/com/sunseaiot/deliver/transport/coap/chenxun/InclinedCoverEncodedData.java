/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.chenxun;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.transport.coap.util.Crc16Utils;
import com.sunseaiot.deliver.transport.coap.util.StringToHexUtil;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 晨讯井盖下行数据
 *
 * @author jyl
 * @date 2019-08-23
 */
public class InclinedCoverEncodedData {
    /**
     * 倾角井盖下行数据下发
     *
     * @param attributeMap 页面下发数据
     * @return String 返回HEX字符串
     */
    public String encodData(Map<String, TypeValue> attributeMap) {
        // 版本
        String type = "01";
        String typeHex = StringToHexUtil.convertStringToHex(type);
        // 指令
        String instructions = "03";
        String instructionsHex = StringToHexUtil.convertStringToHex(instructions);
        // 预留位
        String reserved = "0001";
        String reservedHex = StringToHexUtil.convertStringToHex(reserved);
        // 上报间隔
        String communicationInterval = attributeMap.get("communicationInterval").getStringValue();
        StringBuilder str = new StringBuilder();
        int h = 8;
        // 判断位数，位数不满足8位前面加0补齐
        if (communicationInterval.length() < h) {
            for (int i = 0; i < h - communicationInterval.length(); i++) {
                str.append("0");
            }
        }
        // 上报间隔hex
        String communicationIntervalHex = StringToHexUtil.convertStringToHex(str + communicationInterval);
        // 撤防状态
        String removalSchedule = attributeMap.get("removalSchedule").getStringValue();
        String removalScheduleHex = StringToHexUtil.convertStringToHex(removalSchedule);
        // 撤防开始时间
        String removalDailyStart = attributeMap.get("removalDailyStart").getStringValue();
        String timeh = removalDailyStart.substring(0, 2);
        String timem = removalDailyStart.substring(3, 5);
        int a = Integer.parseInt(timeh);
        int b = Integer.parseInt(timem);
        // 计算UTC时间戳
        long removalDailyStartTime = (a - 8) * 3600 + b * 60;
        // 将时间戳转换成16进制
        String removalDailyStartTime16 = Integer.toHexString((int) removalDailyStartTime);
        StringBuilder strTimes = new StringBuilder();
        // 判断位数，位数不满足8位前面加0补齐
        if (removalDailyStartTime16.length() < h) {
            for (int i = 0; i < h - removalDailyStartTime16.length(); i++) {
                strTimes.append("0");
            }
        }
        String removalDailyStartTimeHex = StringToHexUtil.convertStringToHex(strTimes + removalDailyStartTime16);
        // 撤防开始时间hex
        String removalDailyEnd = attributeMap.get("removalDailyEnd").getStringValue();
        String timeeh = removalDailyEnd.substring(0, 2);
        String timeem = removalDailyEnd.substring(3, 5);
        int c = Integer.parseInt(timeeh);
        int d = Integer.parseInt(timeem);
        // 计算UTC时间戳
        long removalDailyEndTime = (c - 8) * 3600 + d * 60;
        String removalDailyEndTime16 = Integer.toHexString((int) removalDailyEndTime);
        StringBuilder strTimee = new StringBuilder();
        int e = 8;
        // 判断位数，位数不满足8位前面加0补齐
        if (removalDailyEndTime16.length() < e) {
            for (int i = 0; i < e - removalDailyEndTime16.length(); i++) {
                strTimee.append("0");
            }
        }
        String removalDailyEndTimeHex = StringToHexUtil.convertStringToHex(strTimee + removalDailyEndTime16);
        // 连续撤防开始日期
        String removalManyDaysStart = attributeMap.get("removalManyDaysStart").getStringValue();
        // 换算成UTC时间戳
        long removalManyDaysStartdate = Timestamp.valueOf(removalManyDaysStart).getTime() / 1000;
        // 转换成16进制
        String removalManyDaysStartdate16 = Integer.toHexString((int) removalManyDaysStartdate);
        // 布防开始日期hex
        String removalManyDaysStartdateHex = StringToHexUtil.convertStringToHex(removalManyDaysStartdate16);
        // 连续撤防结束日期
        String removalManyDaysEnd = attributeMap.get("removalManyDaysEnd").getStringValue();
        // 换算成UTC时间戳
        long removalManyDaysEnddate = Timestamp.valueOf(removalManyDaysEnd).getTime() / 1000;
        // 转换成16进制
        String removalManyDaysEnddate16 = Integer.toHexString((int) removalManyDaysEnddate);
        // 布防开始日期hex
        String removalManyDaysEnddateHex = StringToHexUtil.convertStringToHex(removalManyDaysEnddate16);
        // 数据长度
        String buffer =
                communicationIntervalHex +
                        removalScheduleHex + removalDailyStartTimeHex +
                        removalDailyEndTimeHex + removalManyDaysStartdateHex +
                        removalManyDaysEnddateHex;
        // 数据长度4位
        int bufferLength = buffer.length();
        String bufferLength16 = Integer.toHexString(bufferLength);
        StringBuilder strbuf = new StringBuilder();
        int f = 4;
        // 判断位数，位数不满足4位前面加0补齐
        if (bufferLength16.length() < f) {
            for (int i = 0; i < f - bufferLength16.length(); i++) {
                strbuf.append("0");
            }
        }
        String bufferLengthHex = StringToHexUtil.convertStringToHex(strbuf + bufferLength16);
        // 不带校验位
        String data = typeHex + instructionsHex + bufferLengthHex + reservedHex + buffer;
        byte[] bytes = Crc16Utils.hexStringToBytes(data);
        // 校验码4位
        int checkCode = Crc16Utils.crc16(bytes, bytes.length);
        // 校验码转成16进制
        String checkCode16 = Integer.toHexString(checkCode);
        // 将16进制转成hex码
        String checkCodeHex = StringToHexUtil.convertStringToHex(checkCode16);
        // 带校验位
        return typeHex + instructionsHex + bufferLengthHex + reservedHex + checkCodeHex + buffer;
    }

    /**
     * 下行时间戳下发
     *
     * @param attributeMap 接收页面下发数据
     * @return String 返回HEX字符串
     */
    public String encodDataMissingReport(Map<String, TypeValue> attributeMap) {
        // 版本
        String type = "01";
        String typeHex = StringToHexUtil.convertStringToHex(type);
        // 指令
        String instructions = "04";
        String instructionsHex = StringToHexUtil.convertStringToHex(instructions);
        // 预留位
        String reserved = "0001";
        String reservedHex = StringToHexUtil.convertStringToHex(reserved);
        String strTime = attributeMap.get("timeStampNow").getStringValue();
        // 以秒为单位
        long strTimel = Timestamp.valueOf(strTime).getTime() / 1000;
        // 转换成16进制
        String str16 = Integer.toHexString((int) strTimel);
        // 转换成hex
        String strHex = StringToHexUtil.convertStringToHex(str16);
        int bufferLength = strHex.length();
        String bufferLength16 = Integer.toHexString(bufferLength);
        StringBuilder strbuf = new StringBuilder();
        int a = 4;
        // 判断位数，位数不满足4位前面加0补齐
        if (bufferLength16.length() < a) {
            for (int i = 0; i < a - bufferLength16.length(); i++) {
                strbuf.append("0");
            }
        }
        String bufferLengthHex = StringToHexUtil.convertStringToHex(strbuf + bufferLength16);
        // 不带校验位
        String data = typeHex + instructionsHex + bufferLengthHex + reservedHex + strHex;
        byte[] bytes = Crc16Utils.hexStringToBytes(data);
        // 校验码4位
        int checkCode = Crc16Utils.crc16(bytes, bytes.length);
        // 校验码转成16进制
        String checkCode16 = Integer.toHexString(checkCode);
        // 将16进制转成hex码
        String checkCodeHex = StringToHexUtil.convertStringToHex(checkCode16);
        return typeHex + instructionsHex + bufferLengthHex + reservedHex + checkCodeHex + strHex;
    }
}