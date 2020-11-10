/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.coap.chenxun;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.coap.util.Crc16Utils;
import com.sunseaiot.deliver.transport.coap.util.StringToHexUtil;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.Map;

/**
 * 井盖数据上行
 *
 * @author jyl
 * @date 2019-09-19
 */
public class InclinedCoverCoapResource extends CoapResource {

    private final UpstreamService upstreamService;

    /**
     * 记录设备IMEI号
     */
    static String deviceName;

    /**
     * 记录上传IP地址
     */
    static String IP_ADDRESS;

    public InclinedCoverCoapResource(String name, UpstreamService upstreamService) {
        super(name);
        this.upstreamService = upstreamService;
    }

    @Override
    public void handlePOST(CoapExchange exchange) {

        IP_ADDRESS = exchange.getSourceAddress().toString();
        // 获取设备原数据
        String playload = exchange.getRequestText();
        // 将设备原数据转换成ascii码
        String strAscii = StringToHexUtil.convertHexToString(playload);
        // 去除校验码
        playload = playload.substring(0, 23) + playload.substring(31);
        byte[] payload = Crc16Utils.hexStringToBytes(playload);
        int intCheckCode = Crc16Utils.crc16(payload, payload.length);
        String originalCheckCode = Integer.toHexString(intCheckCode);
        // 设备上传数据校验码
        String upCheckCode = strAscii.substring(12, 16);
        if (upCheckCode.toUpperCase().equals(originalCheckCode.toUpperCase())) {
            // 解析数据
            InclinedCoverDecodingData qjjgDecodingData = new InclinedCoverDecodingData();
            // 数据正常上报
            String str = "01";
            // 设备漏报
            String str2 = "02";
            int a = 2;
            int b = 4;
            /*
             * 判断上传数据类型，01上报正常数据，02上报漏报数据
             */
            if (str.equals(strAscii.substring(a, b))) {
                Map<String, TypeValue> map = qjjgDecodingData.decodingData(playload);
                // 获取设备imei
                String deviceimei = strAscii.substring(16, 31);
                String uuid = upstreamService.deviceAuth("", deviceimei, "").getDeviceId();
                upstreamService.postDeviceData(uuid, map);
                // 记录IP地址，作为上传漏报数据
                IP_ADDRESS = exchange.getSourceAddress().toString();
                // 记录设备IMEI号，漏报数据需要使用
                deviceName = deviceimei;
                exchange.respond(CoAP.ResponseCode.CONTENT, "OK");
            } else if (str2.equals(strAscii.substring(a, b)) && IP_ADDRESS.equals(exchange.getSourceAddress().toString())) {
                Map<String, TypeValue> map = qjjgDecodingData.decodingDataMissingReport(playload);
                // 获取设备imei
                String uuid = upstreamService.deviceAuth("", deviceName, "").getDeviceId();
                upstreamService.postDeviceData(uuid, map);
            } else {
                exchange.respond(CoAP.ResponseCode.CONTENT, "NG");
            }
        } else {
            exchange.respond(CoAP.ResponseCode.CONTENT, "NG");
        }
    }
}