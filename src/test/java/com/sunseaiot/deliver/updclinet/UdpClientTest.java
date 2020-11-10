package com.sunseaiot.deliver.updclinet;

import com.sunseaiot.deliver.DeliverTransportApplication;
import com.sunseaiot.deliver.dto.DeviceBaseInfo;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.UpstreamService;
import com.sunseaiot.deliver.transport.tcp.util.PayLoadUtil;
import com.sunseaiot.deliver.transport.udp.redis.UdpOperateCacheDao;
import com.sunseaiot.deliver.transport.udp.redis.entity.UdpOperateEntity;
import com.sunseaiot.deliver.transport.udp.redis.impl.NotificationServiceImpl;
import com.sunseaiot.deliver.transport.udp.tenglian.ResolveByWord;
import com.sunseaiot.deliver.transport.udp.util.UdpTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@SpringBootTest(classes = DeliverTransportApplication.class)
public class UdpClientTest {

    private static String headerName = "Authorization";

    private static String headerValue = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlSWQiOiIzMDJiNWRjNS0yOTBiLTQ1YzAtODAxMi0wNTUzZGU1OTNiYzkiLCJpZGVudGl0eSI6ImJneUBzdW5zZWFhaW90LmNvbSIsInRlbmFudElkIjoiMWJlNzgyMzAtZGRlZi0xMWU5LTlmNmQtYTU3ZWM2ZTNiYjFjIiwiZXhwIjoxNTcxOTg4NjgzLCJ1c2VySWQiOiI2NDU5NDdhYy04NWU1LTQxOTYtYWFjYS0zOGI5YTA3MDMzNTkiLCJvcmdhbm5pemF0aW9uSWQiOiIxYmU3ODIzMC1kZGVmLTExZTktOWY2ZC1hNTdlYzZlM2JiMWMifQ.9VF4RsJYEZRdYzd9A574dAfuK3IPzL2wpVE4fptTWDY";

    private static final String baseUrl = "http://localhost:8081/api/v1/device/";

    /**
     * -------通用接口-------
     **/
    // 概览
    private static String general = baseUrl + "general?type=fire";

    // 展示图
    private static String picture1 = baseUrl + "picture/alarm?type=cover";

    private static String picture2 = baseUrl + "picture/state?type=cover";

    private static String picture = baseUrl + "picture?type=fire";

    /**
     * -------消防安全-------
     **/
    // 消防安全折线图
    private static String fireReport = baseUrl + "fire/report/af8801c0-f237-11e9-8172-f727cd0bb746?time=TODAY&type=WATER";

    // 消防安全热力图
    private static String fireHeatMap = baseUrl + "fire/heatMap?time=TODAY&type=OPEN";

    // 消防栓详情
    private static String fireInfo = baseUrl + "fire/info/af8801c0-f237-11e9-8172-f727cd0bb746";

    // 消防历史记录
    private static String fireHistory = baseUrl + "fire/history/af8801c0-f237-11e9-8172-f727cd0bb746?productType=PRESSURE&open=1&water&defense&low=1&pageSize=10&pageIndex=1";

    // 消防管理
    private static String fireManage = baseUrl + "fire/product?name&pageIndex=0&pageSize=10&online&open&low&water&defense";

    // 消防id列表
    private static String fireList = baseUrl + "fire/list";


    /**
     * -------道路安全-------
     **/

    // 井盖id列表
    private static String coverList = baseUrl + "cover/list";

    // 井盖详情
    private static String coverInfo = baseUrl + "cover/info/af8801c0-f237-11e9-8172-f727cd0bb746";

    // 井盖折线图
    private static String coverReport = baseUrl + "cover/report?time=today&type=move";

    // 井盖折线图
    private static String coverHeatMap = baseUrl + "cover/heatMap?time=thirty&type=level";


    /**
     * -------环境监测-------
     **/

    // 环境监测 id 列表
    private static String environmentList = baseUrl + "environment/list";

    // 环境监测详情
    private static String environmentInfo = baseUrl + "environment/info/aa64cb21-c895-12e5-a84d-ef4e9646566b";

    // 环境监测折线图
    private static String environmentReport = baseUrl + "environment/report/aa64cb21-c895-12e5-a84d-ef4e9646566b?time=today&type=direction";

    // 环境监测热力图
    private static String environmentHeatMap = baseUrl + "environment/heatMap?time=today&type=temperature";

    // 环境监测玫瑰图
    private static String environmentRose = baseUrl + "environment/rose?time=today&type=direction";

    /**
     * -------道路卫生-------
     **/

    // 地磁id列表
    private static String rubbishList = baseUrl + "rubbish/list";

    // 道路卫生详情
    private static String rubbishInfo = baseUrl + "rubbish/info/eb43c4be-cdf4-11e9-b36a-0242ac110004";

    // 道路卫生折线图
    private static String rubbishReport = baseUrl + "rubbish/report/eb43c4be-cdf4-11e9-b36a-0242ac110004?time=today&type=open";

    // 道路卫生热力图
    private static String rubbishHeatMap = baseUrl + "rubbish/heatMap?time=ninety&type=open";

    /**
     * -------停车监控-------
     **/
    // 停车监控详情
    private static String parkInfo = baseUrl + "park/info/58c8bbf7-5db0-4bce-bd11-c7542de99673";

    // 停车时长折线图
    private static String parkReport = baseUrl + "park/report?time=today&type=parking";

    // 停车监控热力图
    private static String parkHeatMap = baseUrl + "park/heatMap?time=seven&type=parking";

    // 地磁id列表
    private static String parkList = baseUrl + "park/list";




//    public static void main(String[] args) throws IOException {
//        CloseableHttpClient client = HttpClients.createDefault();
//
//        HttpGet httpGet = new HttpGet(fireList);
//
//        httpGet.addHeader(headerName,headerValue);
//        CloseableHttpResponse Response = client.execute(httpGet);
//        HttpEntity entity = Response.getEntity();
//        String str = EntityUtils.toString(entity, "UTF-8");
//        System.out.println(str);
//        Response.close();
//    }


    @Autowired
    UpstreamService upstreamService;

    //外网
    public static final String OUT = "59.37.56.196";
    public static final Integer OUTPORT = 5683;

    // new
    public static final String OUT2 = "193.112.231.84";
    public static final Integer OUTPORT2 = 9980;

    // 本地测试
    public static final String LOCALHOST = "localhost";
    public static final Integer INPORT = 12012;


    public static void main(String[] args) throws IOException {
        String data = "fafd34033521455fdc00000282".toUpperCase();
        byte[] bytes = UdpTools.hexTobytes(data);
        InetAddress inet = InetAddress.getByName(LOCALHOST);
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, inet, INPORT);
        DatagramSocket ds = new DatagramSocket();
        ds.send(dp);
        ds.close();
    }

//    @Autowired
//    DeviceFireRecordImpl deviceFireRecord;

//    @Test
//    @Disabled
//    void testUpdateIp() {
//        byte[] bytes = "68de02de0268bce7775761040e70000001000b000001390b21152210190000000000002b0b2115221019080400000000280b21152210196003000130002c0b211522101943312e3200002d0b21152210195500000000002a0b21152210190000000092922f0b0867725038e76157000000300b04600655500539430000003f1438393836303631393030303030383235373135302e0b2115221019000600000000310b21152210191148244800007616".getBytes();
//        Map<String, String> basicInfoFromMessage = UdpTools.getBasicInfoFromMessage(bytes);
//        UdpOperateEntity udpOperateEntity = new UdpOperateEntity();
//        udpOperateEntity.setDeviceId("19289281");
//        udpOperateEntity.setValue("216.147.1.66.2083");
//        udpOperateEntity.setActionName("05");
//        udpOperateEntity.setCreateTime(new Date());
//        String downMessage = PackMessage.packMessage(basicInfoFromMessage, udpOperateEntity);
//        System.out.println(downMessage);
//    }

    @Test
    @Disabled

    void testParseByWord() {
        String data = "681201120168C499375619040E700000010004000102280B17103003186403000150002B0B17103003182106000000002D0B1710300318550000000000340B17103003180000000000006916";
        byte[] bytes = UdpTools.hexTobytes(data);
        Map<String, TypeValue> resolve = ResolveByWord.resolve(bytes);
        String jsonStr = PayLoadUtil.map2JsonStr(resolve);
        System.out.println(jsonStr);
    }

    @Test
    @Disabled
    void testBasicMessage() {
        String data = "681201120168C499375619040E700000010004000102280B17103003186403000150002B0B17103003182106000000002D0B1710300318550000000000340B17103003180000000000006916";
        byte[] bytes = UdpTools.hexTobytes(data);
        Map<String, String> basicInfoFromMessage = UdpTools.getBasicInfoFromMessage(bytes);
        System.out.println(basicInfoFromMessage.get("contrChar"));
        System.out.println(basicInfoFromMessage.get("addressPlatform"));
        System.out.println(basicInfoFromMessage.get("afnValue"));
        System.out.println(basicInfoFromMessage.get("fnValue"));
        System.out.println(basicInfoFromMessage.get("framesNo"));
        System.out.println(basicInfoFromMessage.get("verification"));
        System.out.println(basicInfoFromMessage.get("addressDevice"));
    }


    @Test
    @Disabled
    void testAuthService() {
        DeviceBaseInfo deviceBaseInfo = upstreamService.deviceAuth(null, "ZNJGTEST", null);
        System.out.println(deviceBaseInfo.toString());
    }


    @Test
    @Disabled
    void testUdpClientAction() throws IOException {
        byte[] data = ("681201120168C461831265040E700000010002000102280B17103003186403000150002B0B1710300318210600000000" + "D3" + "16").getBytes();
        InetAddress inet = InetAddress.getByName("172.31.16.12");
        DatagramPacket dp = new DatagramPacket(data, data.length, inet, 12012);
        DatagramSocket ds = new DatagramSocket();
        ds.send(dp);
        ds.close();
    }

    @Autowired
    UdpOperateCacheDao udpOperateCacheDao;

    @Test
    @Disabled
    void testSetRedis() {
        udpOperateCacheDao.save("83616512", "batteryAlarm", "30");
    }

    @Autowired
    NotificationServiceImpl notificationService;

    @Test
    @Disabled
    void testOnAction() {
        String deviceId = "1f7667e0-d052-11e9-82ae-8dbd49f2b3e3";
        String actionName = "defense";
        Map<String, TypeValue> parameterMap = new HashMap<>();
        TypeValue typeValue = new TypeValue();
        typeValue.setBoolValue(true);
        parameterMap.put("value", typeValue);
        notificationService.onAction(deviceId, actionName, parameterMap);
    }

    @Test
    @Disabled
    void testFindLatest() {
        UdpOperateEntity cswsb = udpOperateCacheDao.findLatest("77180025");
        log.info(cswsb.toString());
    }

}

