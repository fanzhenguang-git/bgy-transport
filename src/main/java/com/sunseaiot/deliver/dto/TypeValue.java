/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.dto;

import com.sunseaiot.deliver.dao.fieldenum.ValueCase;

/**
 * 数据点值
 * 类似gRPC的oneof结构，boolValue、intValue、doubleValue、stringValue只有一个有值。对其中任意一个设置值都会清除另外三个的值，并对应改变ValueCase。
 * 通过ValueCase字段判断哪个字段有值
 *
 * @author wangyongjun
 * @date 2019/5/23.
 */
public class TypeValue {

    private Boolean boolValue;

    private Long intValue;

    private Double doubleValue;

    private String stringValue;

    private String objectValue;

    private ValueCase valueCase;

    public Boolean getBoolValue() {
        if (this.boolValue == null) {
            throw new IllegalStateException("boolValue is not initialized!");
        }
        return boolValue;
    }

    public Long getIntValue() {
        if (this.intValue == null) {
            throw new IllegalStateException("intValue is not initialized!");
        }
        return intValue;
    }

    public Double getDoubleValue() {
        if (this.doubleValue == null) {
            throw new IllegalStateException("doubleValue is not initialized!");
        }
        return doubleValue;
    }

    public String getStringValue() {
        if (this.stringValue == null) {
            throw new IllegalStateException("stringValue is not initialized!");
        }
        return stringValue;
    }

    public String getObjectValue() {
        if (this.objectValue == null) {
            throw new IllegalStateException("objectValue is not initialized!");
        }
        return objectValue;
    }

    public TypeValue setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
        this.valueCase = ValueCase.BOOLVALUE;
        this.intValue = null;
        this.doubleValue = null;
        this.stringValue = null;
        this.objectValue = null;
        return this;
    }

    public TypeValue setIntValue(long intValue) {
        this.intValue = intValue;
        this.valueCase = ValueCase.INTVALUE;
        this.boolValue = null;
        this.doubleValue = null;
        this.stringValue = null;
        this.objectValue = null;
        return this;
    }

    public TypeValue setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
        this.valueCase = ValueCase.DOUBLEVALUE;
        this.boolValue = null;
        this.intValue = null;
        this.stringValue = null;
        this.objectValue = null;
        return this;
    }

    public TypeValue setStringValue(String stringValue) {
        this.stringValue = stringValue;
        this.valueCase = ValueCase.STRINGVALUE;
        this.boolValue = null;
        this.intValue = null;
        this.doubleValue = null;
        this.objectValue = null;
        return this;
    }

    public TypeValue setObjectValue(String objectValue) {
        this.objectValue = objectValue;
        this.valueCase = ValueCase.OBJECTVALUE;
        this.boolValue = null;
        this.intValue = null;
        this.doubleValue = null;
        this.stringValue = null;
        return this;
    }

    public ValueCase getValueCase() {
        return valueCase;
    }
}
