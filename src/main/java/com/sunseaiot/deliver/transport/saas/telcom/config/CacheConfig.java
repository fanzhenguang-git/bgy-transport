/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 配置缓存
 *
 * @author wangyongjun
 * @date 2019/5/13.
 */
@Configuration
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class CacheConfig {

    /**
     * Telcom accessToken缓存
     * TelcomAdapter启动时调用插件系统的接口获取appId和secret对应关系列表，并分别为它们调用登录api，获取到的accessToken放入该缓存中
     *
     * @return Telcom accessToken缓存对象
     */
    @Bean
    public CaffeineCache telcomAccessTokenCache() {

        return new CaffeineCache(TelcomApiConstant.TELCOM_API_ACCESSTOKEN_CACHE_NAME,
                Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(TelcomApiConstant.TELCOM_API_ACCESSTOKEN_EXPIRE)).build());
    }
}
