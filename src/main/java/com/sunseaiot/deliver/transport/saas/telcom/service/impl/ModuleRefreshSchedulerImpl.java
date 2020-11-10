/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service.impl;

import com.alipay.jarslink.api.ModuleConfig;
import com.alipay.jarslink.api.ModuleLoader;
import com.alipay.jarslink.api.ModuleManager;
import com.alipay.jarslink.api.impl.AbstractModuleRefreshScheduler;
import com.google.common.collect.ImmutableList;
import com.sunseaiot.deliver.transport.saas.telcom.config.AppIdSecretConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Yanfeng Zhang
 * @date: 2019/5/22
 **/
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class ModuleRefreshSchedulerImpl extends AbstractModuleRefreshScheduler {

    /**
     * 模块刷新默认间隔1天,单位秒
     */
    private static final int DEFAULT_REFRESH_DELAY = 60 * 60 * 24;

    private static final String DEFAULT_VERSION = "1.0.0";

    /**
     * 编解码插件jar包路径
     */
    private String pluginDirectory = System.getProperty("user.dir") + "/lib/telcom/";

    @Autowired
    private ModuleManager manager;

    @Autowired
    private ModuleLoader moduleLoader;

    @Autowired
    private AppIdSecretConfig appIdSecretConfig;

    private List<ModuleConfig> moduleConfigs;

    @PostConstruct
    public void init() {
        setModuleLoader(moduleLoader);
        setModuleManager(manager);
        setRefreshDelay(DEFAULT_REFRESH_DELAY);
        moduleConfigs = buildModuleConfigs();
    }

    @Override
    public List<ModuleConfig> queryModuleConfigs() {
        return moduleConfigs;
    }

    private List<ModuleConfig> buildModuleConfigs() {
        List<ModuleConfig> moduleConfigs = new ArrayList<>();
        appIdSecretConfig.getAppInfoMap().forEach((k, v) -> {
            String appId = k;
            String pluginName = v.getPluginName();
            ModuleConfig moduleConfig = new ModuleConfig();
            String pathName = pluginDirectory + pluginName;
            try {
                URL moduleUrl = new URL("file", "", pathName);
                moduleConfig.setName(appId);
                moduleConfig.setEnabled(true);
                moduleConfig.setVersion(DEFAULT_VERSION);
                moduleConfig.setModuleUrl(ImmutableList.of(moduleUrl));
                moduleConfigs.add(moduleConfig);
            } catch (MalformedURLException e) {
                log.warn("Load plugin failed: {}, {}", appId, e.getMessage());
            }
        });
        return moduleConfigs;
    }
}
