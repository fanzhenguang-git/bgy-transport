/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.tcp.netty;

import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.boda.ManholeCoverTransportService;
import com.sunseaiot.deliver.transport.tcp.constant.TcpConstant;
import com.sunseaiot.deliver.transport.tcp.hairuide.HjjcTransportService;
import com.sunseaiot.deliver.transport.tcp.rongjia.TrashTransportUpstreamService;
import com.sunseaiot.deliver.transport.tcp.service.TcpDownstreamService;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * @author fanbaochun
 */
@Slf4j
public class TcpTransportHandler extends ChannelInboundHandlerAdapter implements GenericFutureListener<Future<? super Void>>, TcpDownstreamService {

    private final TrashTransportUpstreamService trashTransportUpstreamService;

    private final ManholeCoverTransportService manholeCoverTransportService;

    private final HjjcTransportService hjjcTransportService;

    private final UpstreamService upstreamService;

    public TcpTransportHandler(UpstreamService upstreamService,
                               TrashTransportUpstreamService trashTransportUpstreamService,
                               ManholeCoverTransportService manholeCoverTransportService,
                               HjjcTransportService hjjcTransportService) {
        this.upstreamService = upstreamService;
        this.trashTransportUpstreamService = trashTransportUpstreamService;
        this.manholeCoverTransportService = manholeCoverTransportService;
        this.hjjcTransportService = hjjcTransportService;
    }

    /**
     * 客户端与服务端创建连接的时候调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Tcp server channel active remote ip {}", ctx.channel().remoteAddress());
    }

    /**
     * 客户端与服务端断开连接时调用
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("tcp server channel inactive...");
        //  NettyConfig.group.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        log.info("tcp channelRead begin,msg {}", msg.toString());
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);
        String messageStr = PayLoadUtil.bytes2Str(req);
        log.info("Trash message {}", messageStr);
        msgHandler(req, ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //log.error("Unexpected Exception", cause);
        ctx.close();
    }

    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {
        log.info("operationComplete");
        // TODO 检查连接
    }

    /**
     * 设备上行数据处理
     */
    private void msgHandler(byte[] bytes, ChannelHandlerContext ctx) {
        try {
            byte head = bytes[0];
            byte head1 = bytes[1];
            byte tail = bytes[bytes.length - 1];
            // 0xaa 0x55 融家垃圾桶
            if (head == TcpConstant.BYTE_CODE_HEAD0 && head1 == TcpConstant.BYTE_CODE_HEAD2) {
                byte[] back = trashTransportUpstreamService.payLoadHandler(bytes, ctx);
                if (null != back && back.length > 0) {
                    ByteBuf byteBuf = Unpooled.copiedBuffer(back);
                    ctx.writeAndFlush(byteBuf);
                }
            } else if (head == TcpConstant.BYTE_CODE_HEAD0 && head1 == TcpConstant.BYTE_CODE_HEAD1) {
                // 0xaa 0x3c开头 0xff帧尾 环境监测
                if (tail == TcpConstant.BYTE_CODE_TAIL2) {
                    hjjcTransportService.payLoadHandler(bytes);
                } else {
                    // 井盖
                    manholeCoverTransportService.payLoadHandler(bytes);
                }
            } else {
                log.warn("Unknown Agreement type");
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}