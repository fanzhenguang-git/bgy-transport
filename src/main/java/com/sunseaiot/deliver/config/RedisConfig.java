/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${deliver.redis.server.host}")
    private String host;

    @Value("${deliver.redis.server.port}")
    private Integer port;

    @Value("${deliver.redis.database}")
    private Integer db;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(host, port);
        standaloneConfig.setDatabase(db);
        JedisConnectionFactory factory = new JedisConnectionFactory(standaloneConfig);
        return factory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}