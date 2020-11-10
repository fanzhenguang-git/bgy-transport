/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service.impl;

import com.alipay.jarslink.api.Module;
import com.alipay.jarslink.api.ModuleManager;
import com.sunseaiot.deliver.transport.saas.telcom.service.CodecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author: Yanfeng Zhang
 * @date: 2019/5/22
 **/
@Slf4j
@Service
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class CodecServiceImpl implements CodecService {

    @Autowired
    private ModuleManager manager;

    @Override
    public String encode(String appId, String data) {
        log.trace("Encode begin. original data [{}]", data);
        Module module = manager.find(appId);
        String encodeData = module.doAction("encode", data);
        log.trace("Encode completed. encode data [{}]", encodeData);
        return encodeData;
    }

    @Override
    public String decode(String appId, String data) {
        log.trace("Decode begin. original data [{}]", data);
        Module module = manager.find(appId);
        String decodeData = module.doAction("decode", data);
        log.trace("Decode completed. decode data [{}]", decodeData);
        return decodeData;
    }
}
