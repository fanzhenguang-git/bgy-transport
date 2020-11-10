/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji;

import com.sunseaiot.deliver.constant.MqttBrokerConstant;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import java.util.UUID;

/**
 * @author hupan
 * @date 2019-08-06.
 */
@Slf4j
@Configuration
@IntegrationComponentScan
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class MqttConfig {

    @Value("${deliver.transport.qj.mqtt-broker.qjurl}")
    private String qjserverUrl;

    @Value("${deliver.transport.qj.mqtt-broker.qjusername}")
    private String qjusername;

    @Value("${deliver.transport.qj.mqtt-broker.qjpassword}")
    private String qjpassword;

    /**
     * @description: 订阅奇迹平台设备信息
     * @author: XJP
     * @param:
     * @return:
     * @date: 2019/8/15
     */
    @Bean
    public MqttConnectOptions qjConnectOptions() {

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(qjusername);
        mqttConnectOptions.setPassword(qjpassword.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{qjserverUrl});
        mqttConnectOptions.setKeepAliveInterval(30);
        return mqttConnectOptions;
    }

    /**
     * @description: 奇迹工厂
     * @author: XJP
     * @param:
     * @return:
     * @date: 2019/8/15
     */
    @Bean
    public MqttPahoClientFactory qjClientFactory() {

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(qjConnectOptions());
        return factory;
    }

    /**
     * @description: 奇迹平台接收通道
     * @author: XJP
     * @param:
     * @return:
     * @date: 2019/8/15
     */
    @Bean
    public MessageChannel qjInputChannel() {
        return new DirectChannel();
    }

    /**
     * @description: 奇迹接收通道
     * @author: XJP
     * @param:
     * @return:
     * @date: 2019/8/15
     */
    @Bean
    public MessageProducer qjInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(getClientId(),
                qjClientFactory(),
                MqttBrokerConstant.TOPIC_QJ_RHZN);

        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(qjInputChannel());
        return adapter;
    }

    private String getClientId() {
        return UUID.randomUUID().toString();
    }
}
