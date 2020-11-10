/*
 * Copyright © 2018 SunseaIoT
 */
package com.sunseaiot.deliver.config;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${deliver.minio.server.host}")
    private String host;

    @Value("${deliver.minio.server.port}")
    private int port;

    @Value("${deliver.minio.server.secure}")
    private boolean secure;

    @Value("${deliver.minio.access-key}")
    private String accessKey;

    @Value("${deliver.minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(host, port, accessKey, secretKey, secure);
    }
}
