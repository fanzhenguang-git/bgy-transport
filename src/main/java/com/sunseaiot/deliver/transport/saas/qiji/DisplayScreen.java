/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 信息屏模块
 *
 * @author: XJP
 * @create: 2019/08/16 14:40
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class DisplayScreen {

    // 打开
    public static final String SCREEN_ON = "open";

    // 关闭
    public static final String SCREEN_OFF = "close";

    // 文字叠加
    public static final String SCREEN_TEXT_OVERLAY = "textOverLay";

    @Autowired
    private QueryAccessToken queryAccessToken;

    /**
     * @description:屏幕开关
     * @author: XJP
     * @param: [devSn, onOff]
     * @return: java.util.Map
     * @date: 2019/8/16 14:39
     */
    public Map onOffSC(String devSn, boolean onOff, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("onOffSC", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/onOffSC";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("onOff", onOff);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 屏幕亮度设置
     * @author: XJP
     * @param: [devSn, brightness]
     * @return: java.util.Map
     * @date: 2019/8/16 14:45
     */
    public Map setBrightness(String devSn, int brightness, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("setBrightness", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/setBrightness";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("brightness", brightness);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 重启
     * @author: XJP
     * @param: [devSn, delay]
     * @return: java.util.Map
     * @date: 2019/8/16 14:46
     */
    public Map reboot(String devSn, int delay, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("reboot", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/reboot";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            // 延期重启时间 (单位S)空则默认3秒
            param.put("delay", delay);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 截屏快照
     * @author: XJP
     * @param: [devSn, picQua, picSize]
     * @return: java.util.Map
     * @date: 2019/8/16 14:51
     */
    public Map captureSC(String devSn, int picQua, int picSize, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("captureSC", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/captureSC";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            // 图片质量  1=高， 2=中，3=低
            param.put("picQua", picQua);
            // 图片大小  1=高， 2=中，3=低
            param.put("picSize", picSize);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 设置音量
     * @author: XJP
     * @param: [devSn, volume]
     * @return: java.util.Map
     * @date: 2019/8/16 14:53
     */
    public Map setVolume(String devSn, int volume, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("setVolume", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/setVolume";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            // 音量
            param.put("volume", volume);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @Description: 上传节目
     * @param: [devSn, file]
     * @return: java.util.Map
     * @auther: XJP
     * @date: 2019-08-19 16:11
     */
    public Map fileUpload(String devSn, String filePath, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("fileUpload", hostPort, appId, appSecret);
            String url = "/fileserver/file-upload";
            File pdfFile = new File(filePath);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostForm(hostPort + url, "file", pdfFile);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                log.error("The control command failed to issue!");
            }
            String msg = object.getString("msg");
            log.info("msg is ={}", msg);
            String body = object.getString("body");
            log.info("body is ={}", body);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 播放
     * @author: XJP
     * @param: [devSn, fileId, md5]
     * @return: java.util.Map
     * @date: 2019/8/16 16:21
     */
    public Map issueProgram(String devSn, String fileId, String md5, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("issueProgram", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/issueProgram";
            Map<String, Object> param = new HashMap<>();
            param.put("accessToken", accessToken);
            param.put("devSn", devSn);
            // 节目ID
            param.put("fileId", fileId);
            // 节目文件MD5
            param.put("md5", md5);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @Description: 停止播放
     * @param: [devSn]
     * @return: java.util.Map
     * @auther: XJP
     * @date: 2019-08-22 16:26
     */
    public Map stopProgram(String devSn, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("stopProgram", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/stopProgram";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 添加策略
     * @author: XJP
     * @param: [devSn, fileId, md5, execTime, endTime, strategyId]
     * @return: java.util.Map
     * @date: 2019/8/16 16:24
     */
    public Map addStrategy(String devSn, String fileId, String md5, String execTime, String endTime, String strategyId, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("issueProgram", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/issueProgram";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            // 节目ID
            param.put("fileId", fileId);
            // 节目文件MD5
            param.put("md5", md5);
            // 策略开始执行时间（Utc格式时间，09:00）
            param.put("execTime", execTime);
            // 策略结束时间（utc格式时间，10:00）
            param.put("endTime", endTime);
            param.put("strategyId", strategyId);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 删除策略
     * @author: XJP
     * @param: [devSn, strategyId]
     * @return: java.util.Map
     * @date: 2019/8/16 16:27
     */
    public Map removeStrategy(String devSn, String strategyId, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("removeStrategy", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/removeStrategy";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("strategyId", strategyId);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 000 code表示成功
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 文字叠加
     * @author: XJP
     * @param: [devSn, text, bottom]
     * @return: java.util.Map
     * @date: 2019/8/16 16:29
     */
    public Map textOverlay(String devSn, String text, boolean bottom, String hostPort, String appId, String appSecret) {
        try {
            String accessToken = queryAccessToken.queryAccessToken("textOverlay", hostPort, appId, appSecret);
            String url = "/iot/infoScreen/textOverlay";
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("text", text);
            param.put("bottom", bottom);
            param.put("accessToken", accessToken);

            //发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            if ("000".equals(object.getString("code"))) {
                log.info("Control command sent!");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }
}
