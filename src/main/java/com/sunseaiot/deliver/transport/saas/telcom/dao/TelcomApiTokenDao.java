/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.dao;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
public interface TelcomApiTokenDao {

    /**
     * 通过appId获取存储的accessToken
     *
     * @param appId appId
     * @return accessToken
     */
    String getAccessTokenByAppId(String appId);

    /**
     * 存储accessToken
     *
     * @param appId       appId
     * @param accessToken accessToken
     */
    void saveAccessToken(String appId, String accessToken);
}
