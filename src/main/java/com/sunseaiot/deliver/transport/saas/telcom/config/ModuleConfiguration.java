/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.config;

import com.alipay.jarslink.api.impl.ModuleLoaderImpl;
import com.alipay.jarslink.api.impl.ModuleManagerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Yanfeng Zhang
 * @date: 2019/5/23
 **/
@Configuration
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class ModuleConfiguration {

    @Bean
    public ModuleLoaderImpl moduleLoader() {
        return new ModuleLoaderImpl();
    }

    @Bean
    public ModuleManagerImpl moduleManager() {
        return new ModuleManagerImpl();
    }
}
