/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.xinhaoda;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.Map;

/**
 * 欣浩达服务端
 *
 * @author jyl
 * @date 2019-09-19
 */
public class GeomagnetismCoapResource extends CoapResource {

    private final UpstreamService upstreamService;

    GeomagnetismDecodData dcDecodData = new GeomagnetismDecodData();

    public GeomagnetismCoapResource(String name, UpstreamService upstreamService) {
        super(name);
        this.upstreamService = upstreamService;
    }

    @Override
    public void handlePOST(CoapExchange exchange) {

        String playload = exchange.getRequestText();
        String deviceName;
        String uuid;
        // 设备状态
        String a7 = "A7";
        int a = 2;
        Map<String, TypeValue> map;
        // 获取标志确认动作类型,切割字符串,获取第16位到第18位
        switch (playload.substring(16, 18).toUpperCase()) {
            // 车辆状态
            case "A8":
                deviceName = playload.substring(0, 15);
                uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                map = dcDecodData.vehicleDepartureOrEntry(playload);
                upstreamService.postDeviceData(uuid, map);
                exchange.respond(CoAP.ResponseCode.CONTENT, playload);
                break;
            // 心跳
            case "A4":
                exchange.respond(CoAP.ResponseCode.CONTENT, playload);
                break;
            // 设备电量
            case "A6":
                deviceName = playload.substring(0, 15);
                uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                map = dcDecodData.batteryLow(playload);
                upstreamService.postDeviceData(uuid, map);
                exchange.respond(CoAP.ResponseCode.CONTENT, playload);
                break;
            // 开机
            case "A7":
                deviceName = playload.substring(0, 15);
                uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                map = dcDecodData.deviceOn(playload);
                upstreamService.postDeviceData(uuid, map);
                exchange.respond(CoAP.ResponseCode.CONTENT, playload);
                break;
            // 关机
            default:
                if (a7.equals(playload.substring(0, a))) {
                    deviceName = playload.substring(12, 27);
                    uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                    map = dcDecodData.deviceOff(playload);
                    upstreamService.postDeviceData(uuid, map);
                    exchange.respond(CoAP.ResponseCode.CONTENT, playload);
                    break;
                }
                break;
        }
    }
}
