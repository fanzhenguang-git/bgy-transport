/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.mqtt.msj;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * MQTT代理订阅
 *
 * @author hupan
 * @date 2019-08-06.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.msj.enable", havingValue = "true")
public class MqttBrokerSubscriber {

    @Autowired
    private MsjTransprotService msjTransprotService;

    @ServiceActivator(inputChannel = "msjInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = Objects.requireNonNull(message.getHeaders().get("mqtt_receivedTopic")).toString();

        String payload = message.getPayload().toString();
        log.trace("mqtt rec, topic:{}", topic);
        msjTransprotService.analysisMessage(topic, payload);
    }
}

