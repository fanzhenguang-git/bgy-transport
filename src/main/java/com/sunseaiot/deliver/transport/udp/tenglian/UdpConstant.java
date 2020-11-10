/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.tenglian;

/**
 * 腾联constant
 *
 * @author qixiaofei
 * @date 2019-09-23.
 */
public class UdpConstant {

    // 腾联协议头文件
    public static final String TENGLIANHEAD = "68";

    // 腾联协议第六个字节
    public static final String SIXTH = "68";

    // 腾联协议控制字
    public static final String CONTRCODE = "4B";

    // 腾联协议地址后一个字节
    public static final String AFTERADDRESS = "04";

    // 腾联协议afn值
    public static final String RTAFNVALUE = "00";

    // 腾联协议尾号
    public static final String LASTCODE = "16";

    // 腾联协议预留长度
    public static final String LENGTH = "0000";

    // 腾联协议fn = 3
    public static final String FNVALUE = "00000400";
}
