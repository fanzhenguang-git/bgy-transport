/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class DeliverTransportApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliverTransportApplication.class, args);
    }
}
