/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.server;

import com.sunseaiot.deliver.transport.udp.tenglian.StartParseTenglian;
import com.sunseaiot.deliver.transport.udp.tenglian.UdpConstant;
import com.sunseaiot.deliver.transport.udp.util.UdpTools;
import com.sunseaiot.deliver.transport.udp.xinhaoda.StartParseGeomagnetism;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.udp.UdpServer;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * udp server
 *
 * @author qixiaofei
 * @date 2019/09/05
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.udp.enable", havingValue = "true")
public class UdpNettyServer {

    @Value("${deliver.transport.udp.bind-host}")
    private String host;

    @Value("${deliver.transport.udp.bind-port}")
    private Integer port;

    @Autowired
    StartParseTenglian startParseTenglian;
    @Autowired
    StartParseGeomagnetism startParseGeomagnetism;

    /**
     * 初始化UdpServer
     */
    @PostConstruct
    public void init() {
        log.info("Starting UDP server...UdpPort={},UdpHost={}", host, port);
        // 初始化 UdpServer
        UdpServer.create()
                .handle((in, out) ->
                        out.sendObject(
                                in.receiveObject()
                                        .map(o -> {
                                                    if (o instanceof DatagramPacket) {
                                                        try {
                                                            DatagramPacket p = (DatagramPacket) o;
                                                            ByteBuf buf = p.content();
                                                            byte[] bytes = new byte[buf.readableBytes()];
                                                            buf.readBytes(bytes);
                                                            String downMessage = resolveMessage(bytes);
                                                            log.debug("return device message={}", downMessage);
                                                            // 厂家要求，线程要休眠500
                                                            // Thread.sleep(500);
                                                            byte[] downBytes;
                                                            InetSocketAddress sender;
                                                            if (null != downMessage) {
                                                                downBytes = UdpTools.hexToByteArray(downMessage);
                                                                sender = p.sender();
                                                            } else {
                                                                downBytes = UdpTools.hexToByteArray("");
                                                                sender = new InetSocketAddress(10086);
                                                            }
                                                            ByteBuf byteBuf = p.content().clear().setBytes(downBytes.length, downBytes).writeBytes(downBytes);
                                                            DatagramPacket datagramPacket = new DatagramPacket(byteBuf.retain(), sender);
                                                            return datagramPacket;
                                                        } catch (Exception e) {
                                                            log.error("UDPNettyServer init Method", e);
                                                            DatagramPacket p = (DatagramPacket) o;
                                                            ByteBuf buf = p.content();
                                                            InetSocketAddress sender = new InetSocketAddress(10086);
                                                            return new DatagramPacket(buf.retain(), sender);
                                                        }
                                                    } else {
                                                        return Mono.error(new Exception("Unexpected type of the " + "message: " + o));
                                                    }
                                                }
                                        )
                        )
                ).host(host).port(port).bindNow(Duration.ofSeconds(30));
        log.info("UDP server started!");
    }

    /**
     * @param bytes:服务器收到的bytes数组
     * @return String:下发指令的报文
     */
    private String resolveMessage(byte[] bytes) {
        log.info("udp server get message={}", UdpTools.receiveHexToString(bytes));
        if ("ab30".equalsIgnoreCase(UdpTools.receiveHexToString(bytes))) {
            return null;
        }
        String head = UdpTools.bytes2StrByIndex(bytes, 0);
        String downMessage;
        String data = UdpTools.receiveHexToString(bytes);
        //本地测试使用
//        data =StringToHexUtil.convertHexToString(data);
        switch (head.toUpperCase()) {
            case UdpConstant.TENGLIANHEAD:
                downMessage = startParseTenglian.parseMessage(bytes);
                break;
            default:
                if ("FA".equals(data.substring(15, 17).toUpperCase())) {
                    String s = startParseGeomagnetism.parseMessage(data);
                    downMessage = s;
                    break;
                }
                downMessage = null;
                break;
        }
        return downMessage;
    }
}