/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.http.sutong.service;

/**
 * 苏通地磁Service
 *
 * @author jyl
 * @date 2019-09-06
 */
public interface GeomagnetismService {

    /**
     * @param str 设备上传数据
     * @return String 返回状态值
     */
    String upPlayLoad(String str);
}