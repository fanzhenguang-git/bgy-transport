/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class GrpcServer {

    @Value("${deliver.grpc.bind-host}")
    private String host;

    @Value("${deliver.grpc.bind-port}")
    private int port;

    @Autowired
    private GrpcDeviceService grpcDeviceService;

    private Server server;

    /**
     * 启动 gRPC grpc
     */
    @PostConstruct
    public void init() {

        try {
            server = ServerBuilder.forPort(port)
                    .addService(grpcDeviceService)
                    .build()
                    .start();
            new Thread(() -> {
                try {
                    blockUntilShutdown();
                } catch (InterruptedException e) {
                    log.error("Start gRPC grpc failed. {}", e);
                    this.stop();
                }
            }).start();
        } catch (Exception e) {
            log.error("Start gRPC grpc failed. {}", e);
            this.stop();
            System.exit(-1);
        }
        log.info("gRPC grpc started.host：{}，port:{}", host, port);
    }

    @PreDestroy
    private void stop() {

        if (server != null) {
            server.shutdown();
            log.info("Stop gRPC grpc completed.");
        }
    }

    private void blockUntilShutdown() throws InterruptedException {

        if (server != null) {
            server.awaitTermination();
        }
    }
}
