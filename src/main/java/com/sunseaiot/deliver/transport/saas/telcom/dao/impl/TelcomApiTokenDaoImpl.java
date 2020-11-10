/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.dao.impl;

import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiConstant;
import com.sunseaiot.deliver.transport.saas.telcom.dao.TelcomApiTokenDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class TelcomApiTokenDaoImpl implements TelcomApiTokenDao {

    private final Cache accessTokenCache;

    @Autowired
    public TelcomApiTokenDaoImpl(CacheManager cacheManager) {
        accessTokenCache = cacheManager.getCache(TelcomApiConstant.TELCOM_API_ACCESSTOKEN_CACHE_NAME);
    }

    @Override
    public String getAccessTokenByAppId(String appId) {
        try {
            return accessTokenCache.get(generateAccessTokenCacheKey(appId), String.class);
        } catch (Exception e) {
            log.warn("Error occurs when get accessToken from cache,appId: " + appId, e);
            return "";
        }
    }

    @Override
    public void saveAccessToken(String appId, String accessToken) {
        try {
            accessTokenCache.put(generateAccessTokenCacheKey(appId), accessToken);
        } catch (Exception e) {
            log.warn("Error occurs when save accessToken to cache,appId: " + appId, e);
        }
    }

    private String generateAccessTokenCacheKey(String appId) {
        return TelcomApiConstant.TELCOM_API_ACCESSTOKEN_CACHE_KEY_PREFIX + appId;
    }
}
