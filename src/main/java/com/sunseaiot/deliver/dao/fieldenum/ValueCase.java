/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dao.fieldenum;

/**
 * @author hupan
 * @date 2019-08-15.
 */
public enum ValueCase {

    /**
     * 布尔 value
     */
    BOOLVALUE,

    /**
     * int64 value
     */
    INTVALUE,

    /**
     * double value
     */
    DOUBLEVALUE,

    /**
     * String value
     */
    STRINGVALUE,

    /**
     * Object value
     * 数据类型为String 但是值为对象存储服务中的存储路径，上层获取值的时候，从对象存储中获取下载连接返回给对方
     */
    OBJECTVALUE,
}
