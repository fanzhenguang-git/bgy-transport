/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.constant;

/**
 * tcp直连设备常量
 *
 * @author fanbaochun
 * @date 2019-09-26
 */
public class TcpConstant {

    /**
     * 帧头 高字节
     */
    public final static byte BYTE_CODE_HEAD0 = (byte) 0xaa;

    /**
     * 帧头 低字节
     */
    public final static byte BYTE_CODE_HEAD1 = (byte) 0x3c;

    public final static byte BYTE_CODE_HEAD2 = (byte) 0x55;

    /**
     * 帧尾 高字节
     */
    public final static byte BYTE_CODE_TAIL0 = (byte) 0xbb;

    /**
     * 帧尾 低字节
     */
    public final static byte BYTE_CODE_TAIL1 = (byte) 0x66;

    public final static byte BYTE_CODE_TAIL2 = (byte) 0xff;

    /**
     * 融家垃圾桶功能编码 - 对时
     */
    public final static byte RJ_BYTE_TIMING_CODE = 0x04;

    /**
     * 融家垃圾桶功能编码- 卡号
     */
    public final static byte RJ_BYTE_CARD_NUMBER_CODE = 0x06;

    /**
     * 融家垃圾桶功能码--实时包
     */
    public final static byte RJ_BYTE_REAL_TIME_CODE = 0x03;

    /**
     * 融家垃圾桶功能码--心跳包
     */
    public final static byte RJ_BYTE_HEARTBEAT_CODE = 0x66;

    /**
     * 融家垃圾桶功能码--重启
     */
    public final static String RJ_RESTART_CODE = "01";

    /**
     * 融家垃圾桶功能码--修改IP
     */
    public final static String RJ_UPDATE_IP_CODE = "81";


    /**
     * 融家垃圾桶指令动作 -- 重启
     */
    public final static String RJ_RESTART = "restart";

    /**
     * 融家垃圾桶指令动作 -- 修改IP
     */
    public final static String RJ_REPLACEMENT_IP = "replaceIp";

    /**
     * 设备记录产品类型厂里
     */
    public final static String PRODUCT_TYPE_RUBBISH = "RUBBISH";

    public final static String PRODUCT_TYPE_COVER = "COVER";

    public final static String PRODUCT_STATISTIC_TRASH = "TRASH_STATISTIC";

    public final static String PRODUCT_STATISTIC_COVER = "COVER_STATISTIC";

    /**
     * 垃圾桶设备
     */
    // 容量满了
    public static final int TRASH_CAPACITY_IS_FULL = 1;
    // 容量未满
    public static final int TRASH_CAPACITY_IS_NOT_FULL = 0;

    // 在线
    public static final int TRASH_IS_ONLINE = 1;

    // 离线
    public static final int TRASH_IS_NOT_ONLINE = 0;

    // 在线
    public static final int COVER_IS_ONLINE = 1;

    // 离线
    public static final int COVER_IS_NOT_ONLINE = 0;
}
