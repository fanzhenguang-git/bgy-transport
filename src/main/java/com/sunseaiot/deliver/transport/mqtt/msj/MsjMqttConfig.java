/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.mqtt.msj;

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
 * MQTT配置
 *
 * @author hupan
 * @date 2019-08-06.
 */
@Slf4j
@Configuration
@IntegrationComponentScan
@ConditionalOnProperty(value = "deliver.transport.msj.enable", havingValue = "true")
public class MsjMqttConfig {

    @Value("${deliver.transport.msj.mqtt-broker.url}")
    private String serverUrl;

    @Value("${deliver.transport.msj.mqtt-broker.username}")
    private String username;

    @Value("${deliver.transport.msj.mqtt-broker.password}")
    private String password;

    /**
     * mqtt 连接配置
     */
    @Bean
    public MqttConnectOptions msjConnectOptions() {

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{serverUrl});
        mqttConnectOptions.setKeepAliveInterval(30);
        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory msjClientFactory() {

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(msjConnectOptions());
        return factory;
    }

    /**
     * mqtt 接收通道
     */
    @Bean
    public MessageChannel msjInputChannel() {
        return new DirectChannel();
    }

    // 配置client，监听的topic
    @Bean
    public MessageProducer msjInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(getClientId(),
                msjClientFactory(),
                MqttBrokerConstant.TOPIC_PUB_DEVICE_STATES,
                MqttBrokerConstant.TOPIC_PUB_IN_TIME_DATA);

        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(msjInputChannel());
        return adapter;
    }

    private String getClientId() {
        return UUID.randomUUID().toString();
    }
}
