/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.config;

import com.sunseaiot.deliver.transport.saas.telcom.dto.TelcomAppInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
@Slf4j
@Getter
@Component
@ConditionalOnProperty(value = "deliver.transport.telcom.enable", havingValue = "true")
public class AppIdSecretConfig {

    /**
     * 支持同时订阅多个
     */
    private final Map<String, TelcomAppInfo> appInfoMap = new HashMap<>();

    /**
     * 初始化电信平台订阅信息
     *
     * @param telcomPlugins
     */
    public AppIdSecretConfig(@Value("${deliver.transport.telcom.plugins}") String telcomPlugins) {

        String[] pluginArray = telcomPlugins.split(",");
        for (int i = 0; i < pluginArray.length; i++) {
            // 电动车 simcom-ebike
            if (pluginArray[i].equals("simcom-ebike")) {
                TelcomAppInfo ebike = new TelcomAppInfo();
                ebike.setAppId("_itS4X43VRfZMQGrXM05Yl5Cjcwa");
                ebike.setAppSecret("d35OCF6fgSbd2hbmvPKAgILUBzQa");
                ebike.setPluginName("Sunsea-VL7000-WaterMeter-1.0.0.jar");
                appInfoMap.put(ebike.getAppId(), ebike);
            }

            // 嘉德烟感
            if (pluginArray[i].equals("jade-detector")) {
                TelcomAppInfo jade = new TelcomAppInfo();
                jade.setAppId("vHvpfRfiW9FYWYZhKTfMmCnEom0a");
                jade.setAppSecret("0KpIIT5t7grD4WivJgIXWfGRX3Qa");
                jade.setPluginName("sunseaiot-RHWLSDJDSD51NB-Smoke-1.0.0.jar");
                appInfoMap.put(jade.getAppId(), jade);
            }

            // 海曼烟感 haiman-detector
            if (pluginArray[i].equals("haiman-detector")) {
                TelcomAppInfo haiman = new TelcomAppInfo();
                haiman.setAppId("P3fdmWdpKYERPLVvQofkqca56jQa");
                haiman.setAppSecret("62aC6gCPJpuxg7xWKVeKWoScBJoa");
                haiman.setPluginName("GSTA-userdefine-UserDataType-1.0.0.jar");
                appInfoMap.put(haiman.getAppId(), haiman);
            }

            // 水表 sunsea-watermeter
            if (pluginArray[i].equals("sunsea-watermeter")) {
                TelcomAppInfo water = new TelcomAppInfo();
                water.setAppId("mR3EuUTc5gxI_28Hd439NGTmvy0a");
                water.setAppSecret("CrDWuR1NfNc9fpZjXyqRpf1DGaMa");
                water.setPluginName("SunSeaaIoT-SSIoT00101J-WaterMeter-1.0.0.jar");
                appInfoMap.put(water.getAppId(), water);
            }

            // 博大井盖 boda-lid
            if (pluginArray[i].equals("boda-lid")) {
                TelcomAppInfo boda = new TelcomAppInfo();
                boda.setAppId("msOGi8r1cCICHa581zGeIszMlF8a");
                boda.setAppSecret("UNXQ7oa4r37SXFCN5c7TKllqEwIa");
                boda.setPluginName("IoT-SC0001-SS-1.0.0.jar");
                appInfoMap.put(boda.getAppId(), boda);
            }

            // 晨讯井盖 cx_tilt_lid
            if (pluginArray[i].equals("cx_tilt_lid")) {
                TelcomAppInfo chenxun = new TelcomAppInfo();
                chenxun.setAppId("Q71fE2o6MRjbmRMzen2ZazbdP5wa");
                chenxun.setAppSecret("pSyg4VGca6NaWPypZ_YQDDMQuAsa");
                chenxun.setPluginName("SunSeaIoT-SSIoTSC0002-Siren-1.0.0.jar");
                appInfoMap.put(chenxun.getAppId(), chenxun);
            }

            // 欣浩达地磁 xhd-geomagnetism
            if (pluginArray[i].equals("xhd-geomagnetism")) {
                TelcomAppInfo xhd = new TelcomAppInfo();
                xhd.setAppId("Y1riK_ipeq4udKnIMynVXisFG1Ya");
                xhd.setAppSecret("mZJIVNwLu4T8s9yHQn6O0ZB0icAa");
                xhd.setPluginName("rhwl-SS_IOT_006-VehicleDetector-1.0.0.jar");
                appInfoMap.put(xhd.getAppId(), xhd);
            }

            // 星火云光交锁 xhy-lock
            if (pluginArray[i].equals("xhy-lock")) {
                TelcomAppInfo xhy = new TelcomAppInfo();
                xhy.setAppId("eYhHvBcjj9EFKZDPZuqH_7oGqrQa");
                xhy.setAppSecret("3dRJ_jKvlqcnfue9fJQBCqXUIQca");
                xhy.setPluginName("3e4b6bf951754e5ab9f130681564f905-sunsealock-sunsealock-1.0.0.jar");
                appInfoMap.put(xhy.getAppId(), xhy);
            }

            // 水浸
            if (pluginArray[i].equals("cy-shuijin")) {
                TelcomAppInfo cy = new TelcomAppInfo();
                cy.setAppId("4uVv8aC0IP5P2f4N6OU8cSEPmfQa");
                cy.setAppSecret("Pg4AII_35AS32iX0JTllUSmXb9Ua");
                cy.setPluginName("5590b4bf9e214cb096bc88974b67bcc7-HSJ100-WaterMeter-1.0.0.jar");
                appInfoMap.put(cy.getAppId(), cy);
            }

            // 博大井盖
            if (pluginArray[i].equals("bd_jinggai")) {
                TelcomAppInfo bd = new TelcomAppInfo();
                bd.setAppId("qpZfq4HyC6myfLJwdcTnx8FWM9Ia");
                bd.setAppSecret("fobo5wwoZrA042IPiPqZZnMazeUa");
                bd.setPluginName("IoT-SC0001-SS-2.0.0.jar");
                appInfoMap.put(bd.getAppId(), bd);
            }

            // 蓝雪路灯
            if (pluginArray[i].equals("lx-ludeng")) {
                TelcomAppInfo lx = new TelcomAppInfo();
                lx.setAppId("3UzD_6IBDk2fCW3o6rSQJ_Tsb0wa");
                lx.setAppSecret("zO8nES7F1AiGmypdRf8ndDLjMt8a");
                lx.setPluginName("LX-SC0005-LAMP-1.0.0.jar");
                appInfoMap.put(lx.getAppId(), lx);
            }

            // 金安科技门磁
            if (pluginArray[i].equals("jakj-mc")) {
                TelcomAppInfo ja = new TelcomAppInfo();
                ja.setAppId("vRM9fD59tUJe7wkmdCyoqRTMsBAa");
                ja.setAppSecret("6sKz_f_dQvhDQFeEUYaSweZQVNga");
                ja.setPluginName("JA-SC0006-DOORCONTACT-1.0.0.jar");
                appInfoMap.put(ja.getAppId(), ja);
            }

            // 万谦垃圾桶
            if (pluginArray[i].equals("wx_ljt")) {
                TelcomAppInfo wq = new TelcomAppInfo();
                wq.setAppId("Ss6KRpVDDAY41bGAAcvo0ZpvEOAa");
                wq.setAppSecret("MGuqy2wS8E5esPJCadneuWaAXoQa");
                wq.setPluginName("WQIOT-SD100N-SmartDustbin-1.0.0.jar");
                appInfoMap.put(wq.getAppId(), wq);
            }

            // 腾联设备插件
            // 消防水压
            if (pluginArray[i].equals("tl_xfsy")) {
                TelcomAppInfo tlsy = new TelcomAppInfo();
                tlsy.setAppId("u1M3S7dovafwMienAcsHyjbo1_oa");
                tlsy.setAppSecret("uCiVQWvZdcgfxA63ORb2YlL7BZsa");
                tlsy.setPluginName("SUNSEA-FCDevice2-NBIoT-1.0.0.jar");
                appInfoMap.put(tlsy.getAppId(), tlsy);
            }

            // 消防闷盖
            if (pluginArray[i].equals("tl_xfmg")) {
                TelcomAppInfo tlmg = new TelcomAppInfo();
                tlmg.setAppId("a3ZqgN5wQXUUSD8QveQdSjklVCoa");
                tlmg.setAppSecret("4rxofmDUrnhS9AfxWiVkbCbKIOUa");
                tlmg.setPluginName("SUNSEA-FCDevice2-NBIoT-1.0.0.jar");
                appInfoMap.put(tlmg.getAppId(), tlmg);
            }

            // 新宏博智能电表插件
            if (pluginArray[i].equals("xhb_meter")) {
                TelcomAppInfo xhb = new TelcomAppInfo();
                xhb.setAppId("S1hoY02l70NmItfUm_lBJ0oN8Yoa");
                xhb.setAppSecret("DuunrSvMo5gAypZed0vWvXNiKnMa");
                xhb.setPluginName("Newhongbo_NBIoTDevice_ElectricityMeter_1.0.0.jar");
                appInfoMap.put(xhb.getAppId(), xhb);
            }

            // 海曼可燃气体 haiman01
            if (pluginArray[i].equals("haiman01")) {
                TelcomAppInfo hm = new TelcomAppInfo();
                hm.setAppId("6zOh4rCxvZG76zHMS0oZ5Tsj8sga");
                hm.setAppSecret("9UxClkJqqgq1Z9fDBwGqrAUwG_Ua");
                hm.setPluginName("GSTA-userdefine-UserDataType-2.0.0.jar");
                appInfoMap.put(hm.getAppId(), hm);
            }

            // 泛海烟感
            if (pluginArray[i].equals("fh_detector")) {
                TelcomAppInfo fh = new TelcomAppInfo();
                fh.setAppId("JGEHrKfB_jg0PfSrS0nfQ5Asfska");
                fh.setAppSecret("aUSY9jEzVNfQX1ETifg3teSQ35wa");
                fh.setPluginName("fanhai-1.0.0.jar");
                appInfoMap.put(fh.getAppId(), fh);
            }
        }
    }

    public Map<String, String> getAll() {
        Map<String, String> map = new HashMap<>();
        appInfoMap.forEach((k, v) -> {
            map.put(k, v.getAppSecret());
        });
        return map;
    }

    public String getSecretByAppId(String appId) {
        TelcomAppInfo telcomAppInfo = appInfoMap.get(appId);
        if (telcomAppInfo == null) {
            throw new RuntimeException("Invalid appId: " + appId);
        }
        return telcomAppInfo.getAppSecret();
    }
}
