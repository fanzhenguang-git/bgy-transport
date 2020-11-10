/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service.impl;

import com.sunseaiot.deliver.transport.saas.telcom.config.AppIdSecretConfig;
import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiCallFailureException;
import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiConstant;
import com.sunseaiot.deliver.transport.saas.telcom.dao.TelcomApiTokenDao;
import com.sunseaiot.deliver.transport.saas.telcom.dto.*;
import com.sunseaiot.deliver.transport.saas.telcom.service.TelcomApiService;
import com.sunseaiot.deliver.transport.saas.telcom.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class TelcomApiServiceImpl implements TelcomApiService {

    private final TelcomApiTokenDao telcomApiTokenDao;

    private final WebClient telcomWebClient;

    private final AppIdSecretConfig appIdSecretConfig;

    public TelcomApiServiceImpl(TelcomApiTokenDao telcomApiTokenDao, WebClient telcomWebClient,
                                AppIdSecretConfig appIdSecretConfig) {
        this.telcomApiTokenDao = telcomApiTokenDao;
        this.telcomWebClient = telcomWebClient;
        this.appIdSecretConfig = appIdSecretConfig;
    }

    @Override
    public CreateDeviceCommandResponse createDeviceCommand(String appId, CreateDeviceCommandRequest request) throws TelcomApiCallFailureException {
        String accessToken = getAccessTokenByAppId(appId);
        try {
            String body = GsonUtil.toJson(request);
            log.debug("Call telcom api,create device command,request,app_key: {},body: {}", appId, body);
            ResponseEntity<String> entity = Objects.requireNonNull(telcomWebClient.post()
                    .uri(TelcomApiConstant.TELCOM_API_CREATE_DEVICE_COMMAND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("app_key", appId)
                    .body(BodyInserters.fromObject(body))
                    .exchange()
                    .block())
                    .toEntity(String.class)
                    .block();
            assert entity != null;
            log.debug("Call telcom api,create device command,response,HttpStatus: {}, body: {}",
                    entity.getStatusCode(), entity.getBody());
            if (entity.getStatusCode().isError()) {
                throw new TelcomApiCallFailureException("HttpStatus: " + entity.getStatusCode() + ",body: " + entity.getBody());
            }
            return GsonUtil.fromJson(entity.getBody(), CreateDeviceCommandResponse.class);
        } catch (Exception e) {
            throw new TelcomApiCallFailureException(e.getMessage(), e);
        }
    }

    @Override
    public SubscribeBusinessDataResponse subscribeBusinessData(String appId, SubscribeBusinessDataRequest request) throws TelcomApiCallFailureException {
        String accessToken = getAccessTokenByAppId(appId);
        try {
            String body = GsonUtil.toJson(request);
            log.debug("Call telcom api,subscribe business data,request,app_key: {},body: {}", appId, body);
            ResponseEntity<String> entity =
                    Objects.requireNonNull(telcomWebClient.post().uri(TelcomApiConstant.TELCOM_API_SUBSCRIBE_BUSINESS_DATA)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .header("app_key", appId)
                            .body(BodyInserters.fromObject(body))
                            .exchange()
                            .block())
                            .toEntity(String.class)
                            .block();
            assert entity != null;
            log.debug("Call telcom api,subscribe business data,response,HttpStatus: {}, body: {}",
                    entity.getStatusCode(), entity.getBody());
            // Telcom api文档说明409是冲突，即通知类型已经被订阅，删除后等待10秒后再重新订阅
            if (entity.getStatusCodeValue() == HttpStatus.CONFLICT.value()) {
                log.warn("Call telcom api,subscribe business data,subscription exist!Delete it and resubscribe");
                unsubscribe(appId, request.getCallbackUrl(), request.getNotifyType());
                Thread.sleep(3000);
                return subscribeBusinessData(appId, request);
            }
            if (entity.getStatusCode().isError()) {
                throw new TelcomApiCallFailureException("HttpStatus: " + entity.getStatusCode() + ",body: " + entity.getBody());
            }
            return GsonUtil.fromJson(entity.getBody(), SubscribeBusinessDataResponse.class);
        } catch (Exception e) {
            throw new TelcomApiCallFailureException(e.getMessage(), e);
        }

    }

    @Override
    public QueryDeviceResponse queryDevice(String appId, QueryDeviceRequest request) throws TelcomApiCallFailureException {
        String accessToken = getAccessTokenByAppId(appId);
        try {
            String deviceId = request.getDeviceId();
            log.debug("Call telcom api,query device,request,app_key: {},deviceId: {}", appId, deviceId);
            ResponseEntity<String> entity =
                    Objects.requireNonNull(telcomWebClient.get().uri(TelcomApiConstant.TELCOM_API_QUERY_DEVICE,
                            deviceId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .header("app_key", appId)
                            .exchange()
                            .block())
                            .toEntity(String.class)
                            .block();
            assert entity != null;
            log.debug("Call telcom api,query device,response,HttpStatus: {}, body: {}", entity.getStatusCode(),
                    entity.getBody());
            if (entity.getStatusCode().isError()) {
                throw new TelcomApiCallFailureException("HttpStatus: " + entity.getStatusCode() + ",body: " + entity.getBody());
            }
            return GsonUtil.fromJson(entity.getBody(), QueryDeviceResponse.class);
        } catch (Exception e) {
            throw new TelcomApiCallFailureException(e.getMessage(), e);
        }

    }

    @Override
    public void unsubscribe(String appId, String callbackUrl, String notifyType) throws TelcomApiCallFailureException {
        String accessToken = getAccessTokenByAppId(appId);
        try {
            log.debug("Call telcom api,delete subscriptions,request,app_key: {},callbackUrl: {},notifyType: {}",
                    appId, callbackUrl, notifyType);
            ResponseEntity<String> entity =
                    Objects.requireNonNull(telcomWebClient.delete().uri(TelcomApiConstant.TELCOM_API_DELETE_SUBSCRIPTIONS)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .header("app_key", appId)
                            .attribute("callbackUrl", callbackUrl)
                            .attribute("notifyType", notifyType)
                            .exchange()
                            .block())
                            .toEntity(String.class)
                            .block();
            assert entity != null;
            log.debug("Call telcom api,delete subscriptions,response,HttpStatus: {}, body: {}",
                    entity.getStatusCode(), entity.getBody());
            if (entity.getStatusCode().isError()) {
                throw new TelcomApiCallFailureException("HttpStatus: " + entity.getStatusCode() + ",body: " + entity.getBody());
            }
        } catch (Exception e) {
            throw new TelcomApiCallFailureException(e.getMessage(), e);
        }
    }

    private String getAccessTokenByAppId(String appId) throws TelcomApiCallFailureException {
        // 从缓存中获取accessToken
        String accessToken = telcomApiTokenDao.getAccessTokenByAppId(appId);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        // 从缓存中获取appId对应的secret
        String secret = appIdSecretConfig.getSecretByAppId(appId);
        // 通过登录接口获取token
        TelcomApiToken telcomApiToken = getTokenByLogin(appId, secret);
        telcomApiTokenDao.saveAccessToken(appId, telcomApiToken.getAccessToken());
        return telcomApiToken.getAccessToken();
    }

    private TelcomApiToken getTokenByLogin(String appId, String secret) throws TelcomApiCallFailureException {
        log.debug("Call telcom api,login,request,appId: {},secret: {}", appId, secret);
        ResponseEntity<String> entity =
                Objects.requireNonNull(telcomWebClient.post().uri(TelcomApiConstant.TELCOM_API_LOGIN_URL)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(BodyInserters.fromFormData("appId", appId).with("secret", secret))
                        .exchange()
                        .block())
                        .toEntity(String.class)
                        .block();
        assert entity != null;
        log.debug("Call telcom api,login,response,HttpStatus: {}, body: {}", entity.getStatusCode(), entity.getBody());
        if (entity.getStatusCode().isError()) {
            throw new TelcomApiCallFailureException("HttpStatus: " + entity.getStatusCode() + ",body: " + entity.getBody());
        }
        return GsonUtil.fromJson(entity.getBody(), TelcomApiToken.class);
    }
}
