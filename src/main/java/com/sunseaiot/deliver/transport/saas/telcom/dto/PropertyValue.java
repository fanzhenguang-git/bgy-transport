/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.dto;

/**
 * 数据点值
 * 类似gRPC的oneof结构，boolValue、intValue、doubleValue、stringValue只有一个有值。对其中任意一个设置值都会清除另外三个的值，并对应改变ValueCase。
 * 通过ValueCase字段判断哪个字段有值
 *
 * @author wangyongjun
 * @date 2019/5/23.
 */
public class PropertyValue {

    private Boolean boolValue;

    private Long intValue;

    private Double doubleValue;

    private String stringValue;

    private ValueCase valueCase;

    public Boolean getBoolValue() {
        if (this.boolValue == null) {
            throw new IllegalStateException("boolValue is not initialized!");
        }
        return boolValue;
    }

    public PropertyValue setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
        this.valueCase = ValueCase.BOOLVALUE;
        this.intValue = null;
        this.doubleValue = null;
        this.stringValue = null;
        return this;
    }

    public Long getIntValue() {
        if (this.intValue == null) {
            throw new IllegalStateException("intValue is not initialized!");
        }
        return intValue;
    }

    public PropertyValue setIntValue(long intValue) {
        this.intValue = intValue;
        this.valueCase = ValueCase.INTVALUE;
        this.boolValue = null;
        this.doubleValue = null;
        this.stringValue = null;
        return this;
    }

    public Double getDoubleValue() {
        if (this.doubleValue == null) {
            throw new IllegalStateException("doubleValue is not initialized!");
        }
        return doubleValue;
    }

    public PropertyValue setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
        this.valueCase = ValueCase.DOUBLEVALUE;
        this.boolValue = null;
        this.intValue = null;
        this.stringValue = null;
        return this;
    }

    public String getStringValue() {
        if (this.stringValue == null) {
            throw new IllegalStateException("stringValue is not initialized!");
        }
        return stringValue;
    }

    public PropertyValue setStringValue(String stringValue) {
        this.stringValue = stringValue;
        this.valueCase = ValueCase.STRINGVALUE;
        this.boolValue = null;
        this.intValue = null;
        this.doubleValue = null;
        return this;
    }

    public ValueCase getValueCase() {
        return valueCase;
    }

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
        STRINGVALUE
    }
}
