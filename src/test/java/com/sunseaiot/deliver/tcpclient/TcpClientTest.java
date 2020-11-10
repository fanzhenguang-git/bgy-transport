package com.sunseaiot.deliver.tcpclient;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.DeliverTransportApplication;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.transport.tcp.entity.TcpDeviceCacheEntity;
import com.sunseaiot.deliver.transport.tcp.netty.client.TcpNettyClient;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import reactor.netty.tcp.TcpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = DeliverTransportApplication.class)
public class TcpClientTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static void main(String[] args) throws Exception {
        // 卡号包
//        byte[] bytes = new byte[]{(byte) 0xaa, 0x55, (byte) 0xfe, 0x06, 0x78, 0x78, 0x64, 0x74, 0x27,
//                (byte) 0x81, 0x38, 0x39, 0x38, 0x36, 0x30, 0x34, 0x30, 0x33,
//                0x31, 0x30, 0x31, 0x38, 0x39, 0x31, 0x34, 0x34, 0x31, 0x31,
//                0x32, 0x30, 0x7a, (byte) 0xbb, 0x66};

        byte[] bytes = new byte[]{(byte)0xaa,0x55,(byte)0xb2,0x03,0x78,0x78,0x64 ,0x74 ,
                0x27 ,(byte)0x81 ,0x19 ,0x33 ,0x36 ,0x2e ,0x36 ,0x36 ,0x38 ,0x33 ,0x30 ,0x33 ,0x00
                ,0x00 ,0x31 ,0x31 ,0x36 ,0x2e ,0x38 ,0x38 ,0x39 ,0x32 ,0x33 ,0x31 ,0x00 ,0x00 ,0x0d ,0x7e ,0x01 ,0x00 ,0x00 ,
                0x08 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,0x00 ,(byte)0x8d ,(byte)0xbb ,0x66};
        // 设备序列号
//        String serialNumberBytes = PayLoadUtil.getBytesStr(4, 10, bytes);
//        byte[] cc = PayLoadUtil.getBytes(4, 10, bytes);
//
//        //aa 55 fe 06 78 78 64 74 27 81 01 05 bb 66
//        byte[] bytes1 = new byte[14];
//        bytes1[0] = (byte) 0xaa;
//        bytes1[1] = 0x55;
//        bytes1[2] = (byte) 0xfe;
//        bytes1[3] = 0x06;
//        int index = 4;
//        for (byte b : cc) {
//            bytes1[index] = b;
//            index++;
//        }
//        bytes1[10] = 0x01;
//        int checkCountNew = PayLoadUtil.getCheckCount1(2, 11, bytes1);
//        bytes1[11] = PayLoadUtil.intToByte(checkCountNew)[0];
//        bytes1[12] = (byte) 0xbb;
//        bytes1[13] = (byte) 0x66;
////        System.out.println(PayLoadUtil.getBytesStr(0,14,bytes1));
//        for (byte b : bytes1
//        ) {
//            System.out.println(b & 0xff);
//        }

        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        TcpNettyClient tcpNettyClient = new TcpNettyClient();
        tcpNettyClient.sendMessage(byteBuf,"localhost",9999);

    }

    @Test
    @Disabled
    void timingPk() {
        // 对时包
        byte[] bytes = new byte[]{(byte) 0xAA, 0x55, (byte) 0xFF, 0x04, 0x17, 0x06
                , 0x66, 0x69, 0x31, (byte) 0x86, (byte) 0xA6, (byte) 0xBB, 0x66};
//        for (byte b: bytes
//             ) {
//            System.out.println(b);
//        }

        System.out.println("------");
        byte checkCount = bytes[10];
        // 计算校验和
        int countNew = PayLoadUtil.getCheckCount1(2, 10, bytes);
        System.out.println(checkCount);
        System.out.println(PayLoadUtil.intToByte(countNew)[0]);
    }

    /**
     * 环境监测上行数据
     */
    @Test
    @Disabled
    void huanjingTest() throws Exception {
        byte[] bytes = new byte[]{(byte) 0xAA, 0x3C, 0x03, 0x11, 0x25, (byte) 0x82, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01
                , 0x00, 0x01, 0x0E, 0x60, 0x00, 0x12, 0x01, 0x03, 0x02, 0x0D, 0x01, (byte) 0xE0, 0x01, 0x41, 0x01
                , 0x67, 0x00, 0x03, 0x00, 0x00, (byte) 0xC4, 0x03, (byte) 0xFF};
        // 终端唯一标识
        String imei = PayLoadUtil.getBytesStr(3, 11, bytes);

        // 数据字节数
        byte charLength = bytes[14];

        int dataStart = 14 + 3 + 1;
        // 有效数据 字节组下标 结束位置 = dataStart + 数据字节（charLength）
        int dataEnd = dataStart + (charLength & 0xff);
        // 有效数据
        byte[] data = PayLoadUtil.getBytes(dataStart, dataEnd, bytes);

//        byte checkCountH = bytes[dataStart + dataEnd & 0xff];
//        byte checkCountL = bytes[dataStart + dataEnd & 0xff + 1];

        byte checkCountH = bytes[dataEnd];
        byte checkCountL = bytes[dataEnd + 1];
        // 计算校验和
        int checkCountNew = PayLoadUtil.getCheckCount1(0, dataEnd, bytes);
        // 校验和 to byte
        byte[] checkCountNewBytes = PayLoadUtil.intToByte(checkCountNew);

        if (checkCountH == checkCountNewBytes[0] && checkCountL == checkCountNewBytes[1]) {
            System.out.println("====");
        }
        System.out.println("imei:" + imei);
        System.out.println("要获取的字节数：" + charLength);
        // 有效数据 字节数组下标 开始位置 = 数据字节数的下标（固定14） + 3个固定字节 + 1
        System.out.println("校验和高字节位置：" + (dataEnd));
        System.out.println("校验和低字节位置：" + (dataEnd + 1));
        System.out.println(checkCountH & 0xff);
        System.out.println(checkCountL & 0xff);
        System.out.println("-----");

        System.out.println("校验和：" + checkCountNew);

        for (byte b : checkCountNewBytes
        ) {
            System.out.println(b & 0xff);
        }
    }

    /**
     * 博大井盖上行数据
     */
    @Test
    @Disabled
    void jgTest() throws Exception {
        byte[] bytes = new byte[]{(byte) 0xaa, 0x3c, 0x05, 0x08, 0x66, (byte) 0x97, 0x10, 0x30, 0x40, 0x17, (byte) 0x93, 0x01, 0x00, 0x01, 0x05, 0x00, 0x00,
                0x48, (byte) 0xfa, 0x66, 0x0b, 0x13, 0x12, 0x00, 0x08, 0x01, 0x04, 0x60, 0x11, 0x30, 0x10, 0x55, 0x00, 0x34};
        // imei
        String imei = PayLoadUtil.getBytesStr(3, 11, bytes);
//        // 数据长度
//        byte dataLength = bytes[14];
//        // 有效数据
//        int dataStart = 14 + 3 + 1;
//        // 有效数据 字节组下标 结束位置 = dataStart + 数据字节（charLength）
//        int dataEnd = dataStart + (dataLength & 0xff);
//        // 有效数据
//        byte[] data = PayLoadUtil.getBytes(dataStart, dataEnd, bytes);
//        byte level = bytes[dataEnd];
//
//        String imsiStr = PayLoadUtil.getBytesStr(dataEnd + 3, dataEnd + 3 + 8, bytes);

        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        TcpNettyClient tcpNettyClient = new TcpNettyClient();
        tcpNettyClient.sendMessage(byteBuf,"localhost",9999);

        System.out.println(imei);
    }

    @Test
    @Disabled
    void redisTest() {
        Map<String, TypeValue> parameterMap = new HashMap<>();
        parameterMap.put("ip", new TypeValue().setStringValue("192.168.7.175"));
        parameterMap.put("port", new TypeValue().setIntValue(9999));

        String downEntityObj = PayLoadUtil.map2JsonStr(parameterMap);

        TcpDeviceCacheEntity tcpDeviceCacheEntity = new TcpDeviceCacheEntity();
        tcpDeviceCacheEntity.setDeviceId("eb43c4be-cdf4-11e9-b36a-0242ac110004");
        tcpDeviceCacheEntity.setReqTime(new Date());
        tcpDeviceCacheEntity.setImei("170666693186");
        tcpDeviceCacheEntity.setTypeValueObj(JSONObject.parseObject(downEntityObj));

        JSONObject jsonObject = new JSONObject();
        jsonObject = (JSONObject) JSONObject.toJSON(tcpDeviceCacheEntity);
        System.out.println(jsonObject.toJSONString());
        TcpDeviceCacheEntity downEntity = JSONObject.parseObject(jsonObject.toJSONString(), TcpDeviceCacheEntity.class);
        System.out.println(downEntity.getDeviceId());
        System.out.println(downEntity.getTypeValueObj().toJSONString());
        System.out.println(downEntity.getReqTime());
        redisTemplate.opsForValue().append("TCP_170666693186", String.valueOf((JSONObject) JSONObject.toJSON(tcpDeviceCacheEntity)));
        ValueOperations<String, String> value = redisTemplate.opsForValue();
        String myValue = value.get("TCP_170666693186");
        System.out.println("1:" + myValue);
    }
}
