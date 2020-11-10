/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.hook;

import com.google.gson.JsonObject;
import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiConstant;
import com.sunseaiot.deliver.transport.saas.telcom.service.HookService;
import com.sunseaiot.deliver.transport.saas.telcom.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 被电信回调的webhook
 *
 * @author wangyongjun
 * @date 2019/5/14.
 */
@Slf4j
@RestController
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class HookController {

    private final HookService hookService;

    public HookController(HookService hookService) {
        this.hookService = hookService;
    }

    @PostMapping(value = TelcomApiConstant.CALL_BACK_URL_PREFIX + "{appId}")
    public String hook(@PathVariable String appId, @RequestBody String body) {
        log.info("Telcom hook,appId: {}, body: {}", appId, body);
        try {
            JsonObject jsonObject = GsonUtil.fromJson(body).getAsJsonObject();
            TelcomApiConstant.NotifyType notifyType = TelcomApiConstant.NotifyType.valueOf(jsonObject.get("notifyType"
            ).getAsString());
            switch (notifyType) {
                case deviceInfoChanged:
                    hookService.handleDeviceInfoChanged(appId, jsonObject);
                    break;
                case deviceDatasChanged:
                    hookService.handleDeviceDatasChanged(appId, jsonObject);
                    break;
                default:
            }
        } catch (Exception e) {
            log.error("Telcom hook,unknown error occurs", e);
        }
        log.info("Telcom hook,end with 200 OK");
        return "ok";
    }
}
