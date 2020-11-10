/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.initialize;

import com.sunseaiot.deliver.transport.saas.telcom.config.AppIdSecretConfig;
import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiCallFailureException;
import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiConstant;
import com.sunseaiot.deliver.transport.saas.telcom.dto.SubscribeBusinessDataRequest;
import com.sunseaiot.deliver.transport.saas.telcom.service.TelcomApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 应用启动完毕后做初始化：将初始化appId、secret数据存入缓存并建立订阅
 *
 * @author wangyongjun
 * @date 2019/5/14.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class Initialize {

    private final TelcomApiService telcomApiService;

    private final AppIdSecretConfig appIdSecretConfig;

    @Value("${deliver.transport.telcom.hook-host}")
    private String callbackHost;

    public Initialize(TelcomApiService telcomApiService, AppIdSecretConfig appIdSecretConfig) {
        this.telcomApiService = telcomApiService;
        this.appIdSecretConfig = appIdSecretConfig;
    }

    /**
     * 系统启动完毕后，将初始化appId、secret数据存入缓存并建立订阅
     *
     * @param event 系统启动完毕事件
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initAfterApplicationStartup(final ApplicationReadyEvent event) {
        try {
            log.info("Initialize application,start....");
            Map<String, String> appIdSecretMap = appIdSecretConfig.getAll();
            log.info("Initialize application,appIdSecretMap need subscribe: " + appIdSecretMap);
            subscribe(appIdSecretMap);
            log.info("Initialize application,end....");
        } catch (Exception e) {
            log.error("Initialize application,unknown error occurs,application stop", e);
            System.exit(-1);
        }
    }

    private void subscribe(Map<String, String> appIdSecretMap) throws TelcomApiCallFailureException {
        for (Map.Entry<String, String> entry : appIdSecretMap.entrySet()) {
            SubscribeBusinessDataRequest subscribeDeviceInfoChangedRequest = new SubscribeBusinessDataRequest()
                    .setAppId(entry.getKey())
                    .setNotifyType(TelcomApiConstant.NotifyType.deviceInfoChanged.name())
                    .setCallbackUrl(callbackHost + TelcomApiConstant.CALL_BACK_URL_PREFIX + entry.getKey());
            SubscribeBusinessDataRequest subscribeDeviceDatasChangedRequest = new SubscribeBusinessDataRequest()
                    .setAppId(entry.getKey())
                    .setNotifyType(TelcomApiConstant.NotifyType.deviceDatasChanged.name())
                    .setCallbackUrl(callbackHost + TelcomApiConstant.CALL_BACK_URL_PREFIX + entry.getKey());
            //telcomApiService.subscribeBusinessData(entry.getKey(), subscribeDeviceInfoChangedRequest);
            //telcomApiService.subscribeBusinessData(entry.getKey(), subscribeDeviceDatasChangedRequest);
        }
    }
}