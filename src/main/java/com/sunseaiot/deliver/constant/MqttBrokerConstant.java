/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.constant;

/**
 * @author hupan
 * @date 2019-05-20.
 */
public class MqttBrokerConstant {

    /**
     * 报警数据实时上报
     */
    public static String TOPIC_PUB_ALARM = "pub/Alarm";

    /**
     * 信号数据上报
     */
    public static String TOPIC_PUB_IN_TIME_DATA = "pub/InTimeData";

    /**
     * 设备运行信息
     */
    public static String TOPIC_PUB_DEVICE_STATES = "pub/DeviceStates";

    /**
     * 平台下发控制
     */
    public static String TOPIC_SUB_SIGNAL_SET_TO_DEV = "sub/SignalSetToDev";

    public static String TOPIC_QJ_RHZN = "rhzn/device/update";
}
