/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.vo;
/**
 * aep地磁指令类
 *
 * @Author: wanglong
 * @Date: 2020/1/10
 **/
public enum GeoCommand {

    CLEAR_CAR(0, "重置无车"),

    CLEAR_RULER(1, "重置标尺"),

    CLOSE(2, "關機"),

    OPEN(3, "强制重启");

    /**
     * code码
     */
    private Integer code;

    /**
     * 信息
     */
    private String msg;

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

    GeoCommand(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static GeoCommand valueOf(int code) {
        GeoCommand[] values = GeoCommand.values();
        for (GeoCommand command : values) {
            if (command.code == code) {
                return command;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
