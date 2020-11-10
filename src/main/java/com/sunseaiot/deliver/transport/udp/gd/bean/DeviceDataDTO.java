/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.bean;


import lombok.Data;

/**
 * 包装智慧生态设备上传的数据
 *
 * @author weishaopeng
 * @date 2019/12/19 16:17
 */
@Data
public class DeviceDataDTO {

    // 网关透传数据过来的时间
    private String time;

    // 透传的设备原始报文
    private String upData;

    // 设备唯一标识(报文前两位地址值)
    private String deviceSerial;

    // 终端eui
    private String moteEui;

    // 网关eui
    private String gwEui;

    // 设备上传的第一个属性名称
    private String attributeName1;

    // 设备上传的第二个属性名称
    private String attributeName2;

    // 设备上传的第一个属性数据
    private Double upValue;

    // 设备上传的第二个属性数据
    private Double upValue2;

    public DeviceDataDTO() {
    }

    public DeviceDataDTO(String time, String upData, String deviceSerial, String moteEui, String gwEui, String attributeName1, String attributeName2, Double upValue, Double upValue2) {
        this.time = time;
        this.upData = upData;
        this.deviceSerial = deviceSerial;
        this.moteEui = moteEui;
        this.gwEui = gwEui;
        this.attributeName1 = attributeName1;
        this.attributeName2 = attributeName2;
        this.upValue = upValue;
        this.upValue2 = upValue2;
    }

    @Override
    public String toString() {
        return "DeviceDataDTO{" +
                "time='" + time + '\'' +
                ", upData='" + upData + '\'' +
                ", deviceSerial='" + deviceSerial + '\'' +
                ", moteEui='" + moteEui + '\'' +
                ", gwEui='" + gwEui + '\'' +
                ", attributeName1='" + attributeName1 + '\'' +
                ", attributeName2='" + attributeName2 + '\'' +
                ", upValue=" + upValue +
                ", upValue2=" + upValue2 +
                '}';
    }
}
