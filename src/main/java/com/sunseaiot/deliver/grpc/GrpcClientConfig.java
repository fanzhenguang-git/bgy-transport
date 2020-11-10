/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.grpc;

import com.sunseaiot.deliver.rpc.application.ApplicationServiceGrpc;
import com.sunseaiot.deliver.rpc.application.ApplicationServiceGrpc.ApplicationServiceBlockingStub;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${deliver.grpc.apollo.host}")
    private String deviceHost;

    @Value("${deliver.grpc.apollo.port}")
    private int devicePort;

    @Bean
    public Channel serverChannel() {
        return ManagedChannelBuilder.forAddress(deviceHost, devicePort).usePlaintext().build();
    }

    @Bean
    public ApplicationServiceBlockingStub originDataServiceBlockingStub(Channel serverChannel) {
        return ApplicationServiceGrpc.newBlockingStub(serverChannel);
    }
}
