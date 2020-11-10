/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.udp.gd.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dao.entity.ProductEntity;
import com.sunseaiot.deliver.dao.repository.ProductRepository;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.transport.udp.gd.dao.entity.DeviceLoRaEntity;
import com.sunseaiot.deliver.transport.udp.gd.dao.repository.DeviceLoRaRepository;
import com.sunseaiot.deliver.transport.udp.gd.service.UdpClientSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * 智慧生态下行服务
 *
 * @author weishaopeng
 * @date 2019/12/25 9:12
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.gd.enable", havingValue = "true")
public class EcologyNotificationServiceImpl implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DeviceLoRaRepository deviceLoRaRepository;

    @Autowired
    private UdpClientSocketService udpClientSocketService;

    @Value("${deliver.transport.gd.tenant-ecology-name}")
    private String ecologyName;

    // 创建DatagramSocket对象
    private DatagramSocket socket;

    {
        try {
            socket = new DatagramSocket(14003);
            // 设置阻塞时长20秒
            // socket.setSoTimeout(1000 * 20);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {
        // 注册该服务类到Map中
        String serviceId = registerService.registerNotification(this);
        // 根据租户名称查询该租户的全部产品
        List<ProductEntity> productEntityList = productRepository.findAllByTenantName(ecologyName);
        // 当该租户产品不为空时
        if (null != productEntityList && productEntityList.size() > 0) {
            productEntityList.forEach(productEntity -> {
                // 根据产品id查找设备
                List<DeviceEntity> deviceEntityList = deviceDao.findByProductId(productEntity.getId());
                // 把设备注册到redis中
                deviceEntityList.forEach(deviceEntity -> registerService.registerDevice(serviceId, deviceEntity.getId()));
            });
        }
    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {
        try {
            EcologyDownStreamData(deviceId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {
        try {
            EcologyDownStreamData(deviceId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 下行数据到LoRa网关,目前生态设备没有对该下行数据的任何处理
     *
     * @param deviceId 设备id
     */
    private void EcologyDownStreamData(String deviceId) throws IOException {
        Optional<DeviceEntity> deviceEntityOptional = deviceDao.findById(deviceId);
        if (deviceEntityOptional.isPresent()) {
            DeviceEntity deviceEntity = deviceEntityOptional.get();
            String serial = deviceEntity.getName();
            if (!StringUtils.isEmpty(serial)) {
                String gwEui = serial.substring(0, 16);
                String deviceSerial = serial.substring(16, serial.length() - 1);
                List<DeviceLoRaEntity> deviceLoRaEntityList = deviceLoRaRepository.findAllByDeviceSerialAndGwEui(deviceSerial, gwEui);
                if (null != deviceLoRaEntityList && deviceLoRaEntityList.size() == 1) {
                    String downStreamJsonStr = getDownStreamJsonStr(deviceLoRaEntityList);
                    udpClientSocketService.downStreamUdp4cityAir(downStreamJsonStr);
                }
            }
        }
        log.info("联动动作下发成功-------------------");
    }

    /**
     * 获取下行包Json字符串
     *
     * @param deviceLoRaEntityList 设备LoRa数据列表
     */
    private String getDownStreamJsonStr(List<DeviceLoRaEntity> deviceLoRaEntityList) {
        DeviceLoRaEntity deviceLoRaEntity = deviceLoRaEntityList.get(0);
        String moteEui = deviceLoRaEntity.getMoteEui();
        // 随机数 1-65535
        int token = new Random().nextInt(65535) + 1;
        // 代表方向
        String dir = "dn";
        // LoRaWAN MAC层的端口号
        int port = 10;
        String upData = deviceLoRaEntity.getUpData();
        upData = new BASE64Encoder().encode(upData.getBytes(StandardCharsets.UTF_8));
        String payload = removeEndWithEqual(upData);
        JSONObject downStreamJson = new JSONObject();
        JSONObject app = new JSONObject();
        JSONObject userdata = new JSONObject();
        userdata.put("dir", dir);
        userdata.put("port", port);
        userdata.put("payload", payload);
        app.put("moteeui", moteEui);
        app.put("token", token);
        app.put("userdata", userdata);
        downStreamJson.put("app", app);
        return downStreamJson.toString();
    }

    /**
     * 应用数据进过base64 编码后，需要去掉末尾所有“=”号再放入 payload中
     *
     * @param string 需要截取的字符串
     * @return 截取后的字符串
     */
    private String removeEndWithEqual(String string) {
        if (string.endsWith("=")) {
            return removeEndWithEqual(string.substring(0, string.length() - 2));
        }
        return string;
    }
}
