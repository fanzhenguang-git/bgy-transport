/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service;

/**
 * @author: Yanfeng Zhang
 * @date: 2019/5/22
 **/
public interface CodecService {

    /**
     * 编码
     *
     * @param appId 应用ID
     * @param data  待编码数据
     * @return 编码后的数据
     */
    String encode(String appId, String data);

    /**
     * 解码
     *
     * @param appId 应用ID
     * @param data  待解码数据
     * @return 解码后的类型
     */
    String decode(String appId, String data);
}
