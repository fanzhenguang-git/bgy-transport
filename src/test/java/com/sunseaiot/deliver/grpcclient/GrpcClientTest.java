/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.grpcclient;

import com.sunseaiot.deliver.DeliverTransportApplication;
import com.sunseaiot.deliver.rpc.device.*;
import com.sunseaiot.deliver.rpc.device.DeviceServiceGrpc.DeviceServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用法：构建grpc客户端，调用自己或其它模块的grpc接口，方便快速排查问题，非单元测试，无法mock。
 */
@Slf4j
@SpringBootTest(classes = DeliverTransportApplication.class)
class GrpcClientTest {

    @Test
    @Disabled
    void testGetDeviceUploadData() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5050).usePlaintext().build();

        DeviceServiceBlockingStub blockingStub = DeviceServiceGrpc.newBlockingStub(channel);

        GetDeviceDataRequest request = GetDeviceDataRequest.newBuilder()
                .addDeviceId("39b1acfa-bf48-11e9-a610-0242ac110004")
                .build();

        GetDeviceDataResponse response = blockingStub.getDeviceData(request);

        log.info("response: {}, {}", response.getStatus().getCode(), response.getDeviceDataList());
    }

    @Test
    @Disabled
    void testPubDeviceAction() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5050).usePlaintext().build();

        DeviceServiceBlockingStub blockingStub = DeviceServiceGrpc.newBlockingStub(channel);

        PushDeviceActionRequest request = PushDeviceActionRequest.newBuilder()
                .setDeviceId("39b1acfa-bf48-11e9-a610-0242ac110004")
                .setActionName("open")
                .build();

        PushDeviceActionResponse response = blockingStub.pushDeviceAction(request);

        log.info("response: {}, {}", response.getStatus().getCode());
    }

//    public static void main(String[] args){
//        System.out.println(System.getProperty("user.dir"));
//    }
}
