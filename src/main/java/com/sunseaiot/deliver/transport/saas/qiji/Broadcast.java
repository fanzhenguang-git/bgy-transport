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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 广播模块-对接奇迹平台时使用
 *
 * @author: XJP
 * @create: 2019/08/16 16:44
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class Broadcast {

    // 设置音量
    public static final String BROADCAST_SET_VOLUME = "setVolume";

    // 播放
    public static final String BROADCAST_PLAY_PROGRAME = "play";

    // 停止播放
    public static final String BROADCAST_STOP_PALY_PROGRAME = "stop";

    @Autowired
    private QueryAccessToken queryAccessToken;

    /**
     * @description: 设置音量
     * @author: XJP
     * @param: [devSn, volume]
     * @return: java.util.Map
     * @date: 2019/8/16 16:54
     */
    public Map setVolume(List<String> devSn, int volume, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("setVolume", hostPort, appId, appSecret);
            String url = "/iot/broadcast/setVolume";
            if (devSn == null) {
                log.info("devSn is empty");
                return null;
            }
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("volume", String.valueOf(volume));
            param.put("accessToken", accessToken);
            // 设置头部
            Map<String, Object> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            // 发送http post请求
            String strRespOnoff = HttpUtil.doHttpPost(hostPort + url, param, headers);
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
     * @description: 播放节目
     * @author: XJP
     * @param: [devSn, fileId]
     * @return: java.util.Map
     * @date: 2019/8/16 16:59
     */
    public Map playFile(String devSn, List<String> fileIds, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("playFile", hostPort, appId, appSecret);
            String url = "/iot/broadcast/playFile";
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("fileId", fileIds);
            param.put("accessToken", accessToken);
            // 设置header
            Map<String, Object> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            // 发送http post请求
            String strRespOnoff = HttpUtil.doHttpPost(hostPort + url, param, headers);
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
     * @description: 停止播放
     * @author: XJP
     * @param: [devSn, fileId]
     * @return: java.util.Map
     * @date: 2019/8/16 17:00
     */
    public Map stop(String devSn, String sId, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("stop", hostPort, appId, appSecret);
            String url = "/iot/broadcast/stop";
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("devSn", devSn);
            param.put("sId", sId);
            param.put("accessToken", accessToken);
            // 设置header
            Map<String, Object> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            // 发送http post请求
            String strRespOnoff = HttpUtil.doHttpPost(hostPort + url, param, headers);
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
     * @Description: 上传MP3文件
     * @param: [devSn, file]
     * @return: java.util.Map
     * @auther: XJP
     * @date: 2019-08-19 16:32
     */
    public Map uploadFile(String devSn, String filePath, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("uploadFile", hostPort, appId, appSecret);
            String url = "/iot/broadcast/uploadFile";
            File pdfFile = new File(filePath);
            Map<String, Object> param = new HashMap<>();
            // param.put("devSn", devSn);
            param.put("accessToken", accessToken);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostForm(hostPort + url, "file"
                    , pdfFile, param);
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
     * @description: 删除MP3文件
     * @author: XJP
     * @param: [fileId]
     * @return: java.util.Map
     * @date: 2019/8/16 17:02
     */
    public Map removeFile(String fileId, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("removeFile", hostPort, appId, appSecret);
            String url = "/iot/broadcast/removeFile";
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("fileId", fileId);
            param.put("accessToken", accessToken);
            // 设置header
            Map<String, Object> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            // 发送http post请求
            String strRespOnoff = HttpUtil.doHttpPost(hostPort + url, param, headers);
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
     * @description: 下发定时任务
     * @author: XJP
     * @param: [taskName, startDay, endDay, taskItemList]
     * @return: java.util.Map
     * @date: 2019/8/16 17:23
     */
    public Map publishTask(String taskName, Date startDay, Date endDay, BroadcastTaskItem taskItemList, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("publishTask", hostPort, appId, appSecret);
            String url = "/iot/broadcast/publishTask";

            Map<String, Object> param = new HashMap<>();
            param.put("taskName", taskName);
            if (null != startDay) {
                param.put("startDay", startDay);
            }
            if (null != endDay) {
                param.put("endDay", endDay);
            }
            param.put("taskItemList", taskItemList);
            param.put("accessToken", accessToken);

            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param);
            // 000 code表示成功
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

    /**
     * @description: 删除定时任务
     * @author: XJP
     * @param: [fileId]
     * @return: java.util.Map
     * @date: 2019/8/16 19:44
     */
    public Map deleteTask(String fileId, String hostPort, String appId, String appSecret) {

        try {
            String accessToken = queryAccessToken.queryAccessToken("deleteTask", hostPort, appId, appSecret);
            String url = "/iot/broadcast/removeFile";

            Map<String, Object> param = new HashMap<>();
            param.put("fileId", fileId);
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
}
