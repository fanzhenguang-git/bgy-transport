/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.tenglian;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.transport.udp.util.UdpTools;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据文档解析
 *
 * @author qixiaofei
 * @date 2019-09-23.
 */
@Slf4j
public class ResolveByWord {

    public static Map<String, TypeValue> resolve(byte[] bytes) {
        Map<String, TypeValue> attributeValueMap = new HashMap<>();
        try {
            log.debug("get into parse");
            // 获取事件数量
            byte eventCount = bytes[18];
            // 数据从19开始解析，前面18个字节是固定的
            int start = 19;
            for (int i = 1; i <= eventCount; i++) {
                if (i == 1) {
                    // 弃用的字节,暂时无用
                    byte lost1 = bytes[start];
                    byte lost2 = bytes[++start];
                    byte lost3 = bytes[++start];
                }
                byte eventErc = bytes[++start];
                byte dataLength = bytes[++start];
                if (eventErc == 47 || eventErc == 48 || eventErc == 63) {
                    int begin = start;
                    switch (eventErc) {
                        case 47:
                            attributeValueMap = erc47(begin, bytes, attributeValueMap);
                            break;
                        case 48:
                            attributeValueMap = erc48(begin, bytes, attributeValueMap);
                            break;
                        case 63:
                            attributeValueMap = erc63(begin, bytes, attributeValueMap);
                            break;
                    }
                } else {
                    String time = UdpTools.bytes2StrByIndex(bytes, start + 5) + UdpTools.bytes2StrByIndex(bytes, start + 4)
                            + UdpTools.bytes2StrByIndex(bytes, start + 3) + UdpTools.bytes2StrByIndex(bytes, start + 2) + UdpTools.bytes2StrByIndex(bytes, start + 1);
                    int begin = start + 6;
                    switch (eventErc) {
                        case 40:
                            attributeValueMap = erc40(begin, bytes, attributeValueMap);
                            break;
                        case 42:
                            attributeValueMap = erc42(begin, bytes, attributeValueMap);
                            break;
                        case 43:
                            attributeValueMap = erc43(begin, bytes, attributeValueMap);
                            break;
                        case 44:
                            attributeValueMap = erc44(begin, bytes, attributeValueMap);
                            break;
                        case 45:
                            attributeValueMap = erc45(begin, bytes, attributeValueMap);
                            break;
                        case 46:
                            attributeValueMap = erc46(begin, bytes, attributeValueMap);
                            break;
                        case 49:
                            attributeValueMap = erc49(begin, bytes, attributeValueMap);
                            break;
                        case 50:
                            attributeValueMap = erc50(begin, bytes, attributeValueMap);
                            break;
                        case 52:
                            attributeValueMap = erc52(begin, bytes, attributeValueMap);
                            break;
                        case 54:
                            attributeValueMap = erc54(begin, bytes, attributeValueMap);
                            break;
                        case 57:
                            attributeValueMap = erc57(begin, bytes, attributeValueMap);
                            break;
                        case 58:
                            attributeValueMap = erc58(begin, bytes, attributeValueMap);
                            break;
                        case 60:
                            attributeValueMap = erc60(begin, bytes, attributeValueMap);
                            break;
                        case (byte) 200:
                            attributeValueMap = erc200(begin, bytes, attributeValueMap);
                            break;
                        default:
                            log.info("文档中没有的erc事件，erc={}", eventErc);
                            break;
                    }
                    attributeValueMap.put("time", new TypeValue().setStringValue(time));
                }
                start += dataLength;
            }
        } catch (Exception e) {
            log.error("solve error={}", e.toString());
        }


        return attributeValueMap;
    }

    /**
     * 电池电量
     */
    public static Map<String, TypeValue> erc40(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String batteryVoltage = UdpTools.bytes2StrByIndex(bytes, begin + 1) + "." + UdpTools.bytes2StrByIndex(bytes, begin);
            String batteryPercent = UdpTools.bytes2StrByIndex(bytes, begin + 3) + UdpTools.bytes2StrByIndex(bytes, begin + 2);
            String alarmInfo = UdpTools.bytes2StrByIndex(bytes, begin + 4);
            attributeValueMap.put("batteryVoltage", new TypeValue().setDoubleValue(Double.valueOf(batteryVoltage)));
            attributeValueMap.put("batteryPercent", new TypeValue().setIntValue(Integer.valueOf(batteryPercent)));
            attributeValueMap.put("alarmInfo", new TypeValue().setDoubleValue(Double.parseDouble(alarmInfo)));
        } catch (Exception e) {
            log.error("erc40 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 水压数据
     */
    public static Map<String, TypeValue> erc42(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String mpa = UdpTools.bytes2StrByIndex(bytes, begin + 3) + UdpTools.bytes2StrByIndex(bytes, begin + 2) + UdpTools.bytes2StrByIndex(bytes, begin + 1) + UdpTools.bytes2StrByIndex(bytes, begin + 0);
            attributeValueMap.put("mpa", new TypeValue().setDoubleValue(Double.parseDouble(mpa)));
        } catch (NumberFormatException e) {
            log.error("erc42 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 信号强度
     */
    public static Map<String, TypeValue> erc43(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String signalStrength = UdpTools.bytes2StrByIndex(bytes, begin);
            String SNR = UdpTools.bytes2StrByIndex(bytes, begin + 2) + UdpTools.bytes2StrByIndex(bytes, begin + 1);
            String snrType = UdpTools.bytes2StrByIndex(bytes, begin + 5);
            attributeValueMap.put("signalInfo", new TypeValue().setIntValue(Long.parseLong(signalStrength)));
            attributeValueMap.put("SNR", new TypeValue().setIntValue(Long.parseLong(SNR)));
            attributeValueMap.put("snrType", new TypeValue().setBoolValue(Boolean.valueOf(snrType)));
        } catch (NumberFormatException e) {
            log.error("erc43 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 软件版本
     */
    public static Map<String, TypeValue> erc44(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String ascVersion =
                    UdpTools.bytes2StrByIndex(bytes, begin) + UdpTools.bytes2StrByIndex(bytes, begin + 1) + UdpTools.bytes2StrByIndex(bytes, begin + 2) + UdpTools.bytes2StrByIndex(bytes, begin + 3);
            String version = UdpTools.convertHexToString(ascVersion);
            attributeValueMap.put("version", new TypeValue().setStringValue(version));
        } catch (Exception e) {
            log.error("erc44 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 布防撤防状态
     */
    public static Map<String, TypeValue> erc45(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String defense = UdpTools.bytes2StrByIndex(bytes, begin);
            attributeValueMap.put("defense", new TypeValue().setBoolValue(defense.equals("55")));
        } catch (Exception e) {
            log.error("erc45 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 心跳
     */
    public static Map<String, TypeValue> erc46(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {

            String heart = UdpTools.bytes2StrByIndex(bytes, begin + 1) + UdpTools.bytes2StrByIndex(bytes, begin);
            attributeValueMap.put("heart", new TypeValue().setIntValue(Integer.valueOf(heart)));
        } catch (NumberFormatException e) {
            log.error("erc46 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * IMEI
     */
    public static Map<String, TypeValue> erc47(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String IMEI = UdpTools.bytes2StrByIndex(bytes, begin + 1) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 2) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 3) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 4) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 5) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 6) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 7) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 8);
            attributeValueMap.put("IMEI", new TypeValue().setStringValue(IMEI));
        } catch (Exception e) {
            log.error("erc47 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * IMSI
     */
    public static Map<String, TypeValue> erc48(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String IMSI = UdpTools.bytes2StrByIndex(bytes, begin + 1) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 2) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 3) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 4) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 5) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 6) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 7) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 8);
            attributeValueMap.put("IMSI", new TypeValue().setStringValue(IMSI));
        } catch (Exception e) {
            log.error("erc48 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * cellId
     */
    public static Map<String, TypeValue> erc49(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String cellId = UdpTools.bytes2StrByIndex(bytes, begin) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 1) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 2) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 3);
            attributeValueMap.put("cellId", new TypeValue().setStringValue(cellId));
        } catch (Exception e) {
            log.error("erc49 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 消防闷盖开盖
     */
    public static Map<String, TypeValue> erc50(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            attributeValueMap.put("openCover", new TypeValue().setBoolValue(true));
        } catch (Exception e) {
            log.error("erc50 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 消防取水
     */
    public static Map<String, TypeValue> erc52(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            attributeValueMap.put("getWater", new TypeValue().setBoolValue(Boolean.TRUE));
        } catch (Exception e) {
            log.error("erc52 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 消防关水
     */
    public static Map<String, TypeValue> erc200(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            attributeValueMap.put("getWater", new TypeValue().setBoolValue(Boolean.FALSE));
        } catch (Exception e) {
            log.error("erc200 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 电池电压低报警
     */
    public static Map<String, TypeValue> erc54(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        //字节[0-1]：当前电池电压;字节[2-3]: 当前电池电量百分比;字节[4-5]:当前电量告警规则;
        //当前电池电量百分比算法：电池电量百分比 = （采样电压-3.1）/ 0.5  *  100
        return attributeValueMap;
    }

    /**
     * 消防水压压力开关状态
     */
    public static Map<String, TypeValue> erc57(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String low = UdpTools.bytes2StrByIndex(bytes, begin);
            attributeValueMap.put("low", new TypeValue().setBoolValue(low.equals("00")));
        } catch (Exception e) {
            log.error("erc57 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 消防水箱水位值
     */
    public static Map<String, TypeValue> erc60(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String firebox = UdpTools.bytes2StrByIndex(bytes, begin + 1) + UdpTools.bytes2StrByIndex(bytes, begin);
            attributeValueMap.put("waterLevel", new TypeValue().setDoubleValue(Integer.parseInt(firebox) / 100));
        } catch (NumberFormatException e) {
            log.error("erc60 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * 消防水箱水位状态
     */
    public static Map<String, TypeValue> erc58(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String fireboxStatus = UdpTools.bytes2StrByIndex(bytes, begin);
            attributeValueMap.put("fireboxStatus", new TypeValue().setBoolValue(fireboxStatus.equals(00)));
        } catch (Exception e) {
            log.error("erc58 error={}", e.toString());
        }
        return attributeValueMap;
    }

    /**
     * ICCID
     */
    public static Map<String, TypeValue> erc63(int begin, byte[] bytes, Map<String, TypeValue> attributeValueMap) {
        try {
            String iccID = UdpTools.bytes2StrByIndex(bytes, begin + 1) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 2) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 3) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 4) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 5) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 6) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 7) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 8) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 9) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 10) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 11) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 12) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 13) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 14) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 15) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 16) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 17) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 18) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 19) +
                    UdpTools.bytes2StrByIndex(bytes, begin + 20);
            String iccId = UdpTools.convertHexToString(iccID);
            attributeValueMap.put("iccId", new TypeValue().setStringValue(iccId));
        } catch (Exception e) {
            log.error("erc63 error={}", e.toString());
        }
        return attributeValueMap;
    }
}
