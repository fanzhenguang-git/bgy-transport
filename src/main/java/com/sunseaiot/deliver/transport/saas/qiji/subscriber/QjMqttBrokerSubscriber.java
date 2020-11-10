/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.subscriber;

import com.sunseaiot.deliver.transport.saas.qiji.upstream.QjUpstreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class QjMqttBrokerSubscriber {

    @Autowired
    private QjUpstreamService qjUpstreamService;

    @ServiceActivator(inputChannel = "qjInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {

        String topic = null;
        if (message.getHeaders().get("mqtt_receivedTopic") != null) {
            topic = message.getHeaders().get("mqtt_receivedTopic").toString();
        }
        String payload = null;
        if (message.getPayload() != null) {
            payload = message.getPayload().toString();
        }
        log.trace("qj mqtt rec, topic:{}", topic);
        qjUpstreamService.analysisMessage(topic, payload);
    }
}