/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.constant.EntityConstant;
import com.sunseaiot.deliver.exception.DeliverError;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.transport.saas.qiji.broadcasttask.TaskInfo;
import com.sunseaiot.deliver.transport.saas.qiji.upstream.QjUpstreamService;
import com.sunseaiot.deliver.transport.saas.qiji.util.FtpCli;
import com.sunseaiot.deliver.util.HttpUtil;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 雷拓广播设备直接控制类
 *
 * @author: xwb
 * @create: 2019/09/29 19:20
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class BroadCastDirectControl {

    // 设置音量
    public static final String SET_VOLUME = "setVolume";

    // 播放
    public static final String PLAY_PROGRAME = "play";

    // 停止播放
    public static final String STOP_PALY_PROGRAME = "stop";

    // 上传节目
    public static final String UPLOAD_PROGRAME = "upload";

    // 新建定时任务
    public static final String TASK_CREATE = "taskCreate";

    // 启动定时任务
    public static final String TASK_MANUAL_START = "taskManualStart";

    // 定时任务停止
    public static final String TASK_MANUAL_STOP = "taskManualStop";

    // 定时任务删除
    public static final String TASK_DELETE = "taskDelete";

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private QjUpstreamService qjUpstreamService;

    /**
     * @description: 登录广播获取jessionId
     * @author: xwb
     * @param: [user, password]
     * @return: java.util.Map
     * @date: 2019/9/29 09:45
     */

    public String login(String user, String password, String hostPort) {

        String jsessionid = null;
        try {
            String url = "/login";
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("User", user);
            param.put("Passwd", password);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功 示例{"Ret":0,"Remark":"OK","JSessionID":"21A22EC8E27C11E9820B507B9D188393"}
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                jsessionid = object.getString("JSessionID");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return jsessionid;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 获取终端清单
     * @author: xwb
     * @param: [id, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 11:05
     */
    public List<Integer> getTermIds(Integer tNumber, String JSessionID, String hostPort) {

        try {
            List<Integer> resultlist = null;
            String url = "/getTermIds";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            if (tNumber != null) {
                param.put("TNumber", tNumber);
            }
            // 设置header
            Map<String, Object> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPost(hostPort + url, param, headers);
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                // 终端Ids
                String termIDs = object.getString("TermIds");
                JSONArray jsonArray = JSONArray.parseArray(termIDs);
                resultlist = jsonArray.toJavaList(Integer.class);
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return resultlist;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 获取终端状态
     * @author: xwb
     * @param: [tNumber, JSessionID, TermIds]
     * @return: java.util.Map
     * @date: 2019/9/29 14:22
     */
    public String getTermState(Integer tNumber, String JSessionID, List<Integer> termIds, String hostPort) {

        try {
            String terms = null;
            String url = "/getTermState";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            if (tNumber != null) {
                param.put("TNumber", tNumber);
            }
            param.put("TermIDs", termIds);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                terms = object.getString("Terms");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return terms;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 设置音量
     * @author: xwb
     * @param: [tNumber, JSessionID, termIds, volume]
     * @return: java.util.Map
     * @date: 2019/9/29 15:10
     */
    public Map setTermVol(Integer tNumber, String JSessionID, List<Integer> termIds, int volume, String hostPort) {

        try {
            String url = "/TermVolSet";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            if (tNumber != null) {
                param.put("TNumber", tNumber);
            }
            param.put("TermIds", termIds);
            param.put("Volume", volume);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
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
     * @description: 上传mp3
     * @author: xwb
     * @param: [type, filePath, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 17:52
     */
    public Map uploadFile(String orgFileName, String JSessionID, String hostPort) {

        try {
            String url = "/FileUpload";
            url = url + ";JSESSIONID=" + JSessionID;
            Map<String, Object> param = new HashMap<>();
            param.put("Type", 1);
            // 发送http post请求
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                /**
                 * 获取文件上传请求返回参数
                 * {"Ret":0,"Remark":"OK","FileId":"24fef838e29c11e98002507b9d188393",
                 * "FtpUrl":"ftp://192.168.18.220:21//24fef838e29c11e98002507b9d188393.mp3",
                 * "FtpUsr":"websvr","FtpPwd":"webcast"}
                 */
                // 上传文件
                String fileId = object.getString("FileId");
                System.out.println(fileId);
                FtpCli ftpCli = null;
                boolean uploadsuccess = true;
                try {
                    String[] hsplit = hostPort.split(":");
                    String host = hsplit[1].substring(2, hsplit[1].length());
                    ftpCli = FtpCli.createFtpCli(host, 21, "websvr", "webcast", "UTF-8");
                    ftpCli.connect();
                    // 获取文件输入流
                    InputStream inputStream = minioClient.getObject(EntityConstant.MINIO_BUCKET_BROADCAST, orgFileName);
                    // File file = new File(filePath);
                    // ftpCli.upload(fileId+".mp3",file);
                    ftpCli.storeFile(fileId + ".mp3", inputStream);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    uploadsuccess = false;
                } finally {
                    if (ftpCli != null) {
                        ftpCli.disconnect();
                    }
                }
                // 创建节目媒体关联fileId
                if (uploadsuccess) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MLCreateNode(JSessionID, 0, 1, orgFileName, fileId, hostPort);
                }
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
     * @description: 查看节目列表
     * @author: xwb
     * @param: [JSessionID, fileName]
     * @return: java.util.Map
     * @date: 2019/9/29 20:28
     */
    public String MLListDir(String JSessionID, String hostPort) {

        try {
            String items = null;
            String url = "/MLListDir";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("DirId", 0);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                items = object.getString("Items");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 创建节目媒体
     * @author: xwb
     * @param: [JSessionID, parentId, type, name, fileId]
     * @return: java.util.Map
     * @date: 2019/9/29 20:27
     */
    public Map MLCreateNode(String JSessionID, Integer parentId, Integer type, String name, String fileId, String hostPort) {

        try {
            String url = "/MLCreateNode";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("ParentId", parentId);
            param.put("Type", type);
            param.put("Name", name);
            param.put("FileId", fileId);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                String id = object.getString("ID");
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
     * @description: 删除mp3
     * @author: xwb
     * @param: [pid, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 17:52
     */
    public Map deleteMp3(String JSessionID, int pid, String hostPort) {

        try {
            String url = "/MLDelProg";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("ID", pid);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
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
     * @description: 播放mp3
     * @author: xwb
     * @param: [Tids, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 18:59
     */
    public Map ShortVoiceSpeech(List<Integer> tids, int playPrior, String fileId, String JSessionID, String hostPort) {

        try {
            String url = "/ShortVoiceSpeech";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            // 终端ID列表
            param.put("Tids", tids);
            // 播放优先级0-1000
            param.put("PlayPrior", playPrior);
            // 文件ID
            param.put("FileId", fileId);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                String sid = object.getString("Sid");
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
     * @description: 播放mp3
     * @author: xwb
     * @param: [Tids, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 18:59
     */
    public Map RealPlayStart(List<Integer> tids, String time, int playPrior, String JSessionID, String hostPort) {

        try {
            String url = "/RealPlayStart";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            // 终端ID列表
            param.put("Tids", tids);
            param.put("Time", time);
            // 播放优先级0-1000
            param.put("PlayPrior", playPrior);
            // 文件ID
            param.put("AudioFormat", 2);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                String sid = object.getString("Sid");
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
     * @author: xwb
     * @param: [sid, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 19:12
     */
    public Map stopPlayMp3(String sid, String JSessionID, String hostPort) {

        try {
            String url = "/RealPlayStop";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            // 终端ID列表
            param.put("Sid", sid);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
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
     * @description: 定时任务创建
     * @author: xwb
     * @param: [JSessionID, taskInfo]
     * @return: java.util.Map
     * @date: 2019/9/29 20:01
     */
    public Map taskCreate(String JSessionID, TaskInfo taskInfo, String hostPort) {

        try {
            String url = "/TaskCreate";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("Item", taskInfo);
            System.out.println(JSONObject.toJSONString(param));
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                String taskID = object.getString("TaskID");
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
     * @description: 获取任务
     * @author: xwb
     * @param: [JSessionID, taskInfo]
     * @return: java.util.Map
     * @date: 2019/10/8 20:49
     */
    public Integer taskList(String JSessionID, String taskName, String hostPort) {

        try {
            Integer taskId = null;
            String url = "/TaskList";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            System.out.println(JSONObject.toJSONString(param));
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                String items = object.getString("Items");
                JSONArray jsonArray = JSONArray.parseArray(items);
                if (jsonArray.size() == 0) {
                    return null;
                }
                for (int i = 0; i < jsonArray.size(); i++) {
                    Object o = jsonArray.get(i);
                    JSONObject prg = JSONObject.parseObject(o.toString());
                    if (taskName.equals(prg.getString("Name"))) {
                        String id = prg.getString("ID");
                        taskId = Integer.parseInt(id);
                        break;
                    }
                }
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return taskId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 定时任务删除
     * @author: xwb
     * @param: [JSessionID, taskId]
     * @return: java.util.Map
     * @date: 2019/9/29 20:03
     */
    public Map taskDelete(String JSessionID, int taskId, String hostPort) {

        try {
            String url = "/TaskDelete";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("TaskID", taskId);
            // 终端ID列表
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
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
     * @description: 定时任务启动
     * @author: xwb
     * @param: [JSessionID, taskId]
     * @return: java.util.Map
     * @date: 2019/9/29 20:04
     */
    public Map TaskManualStart(String JSessionID, int taskId, String hostPort) {

        try {
            String url = "/TaskManualStart";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("TaskID", taskId);
            // 终端ID列表
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
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
     * @description: 定时任务停止
     * @author: xwb
     * @param: [JSessionID, taskId]
     * @return: java.util.Map
     * @date: 2019/9/29 20:05
     */
    public Map TaskManualStop(String JSessionID, int taskId, String hostPort) {

        try {
            String url = "/TaskManualStop";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            param.put("TaskID", taskId);
            // 终端ID列表
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
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
     * @description: 创建文件广播会话
     * @author: xwb
     * @param: [Tids, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 18:59
     */
    public String FileSessionCreate(List<Integer> tids, int playPrior, String name, String JSessionID, String hostPort) {

        try {
            String sid = null;
            String url = "/FileSessionCreate";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            // 终端ID列表
            param.put("Tids", tids);
            // 播放优先级0-1000
            param.put("PlayPrior", playPrior);
            // 会话名称
            param.put("Name", name);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                sid = object.getString("Sid");
            } else {
                String msg = object.getString("msg");
                log.error("The control command failed to issue!:{}", msg);
            }
            return sid;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeliverException(DeliverError.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * @description: 播放媒体库文件
     * @author: xwb
     * @param: [Tids, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 18:59
     */
    public Map FileSessionSetProg(int sid, int progId, String JSessionID, String hostPort) {

        try {
            String url = "/FileSessionSetProg";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            // 终端ID列表
            param.put("Sid", sid);
            // 播放优先级0-1000
            param.put("ProgId", progId);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
                log.info("Control command sent!");
                String totalTime = object.getString("TotalTime");

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
     * @description: 删除会话
     * @author: xwb
     * @param: [Tids, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 18:59
     */
    public Map FileSessionDestory(int sid, String JSessionID, String hostPort) {

        try {
            String url = "/FileSessionDestory";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            // 会话id
            param.put("Sid", sid);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
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
     * @description: 文件广播播放控制
     * @author: xwb
     * @param: [Tids, JSessionID]
     * @return: java.util.Map
     * @date: 2019/9/29 18:59
     */
    public Map FileSessionSetStat(int sid, int status, String JSessionID, String hostPort) {

        try {
            String url = "/FileSessionSetStat";
            url = url + ";JSESSIONID=" + JSessionID;
            // 设置参数
            Map<String, Object> param = new HashMap<>();
            // 会话id
            param.put("Sid", sid);
            // 0-stop 1- play 2- pause
            param.put("Status", status);
            // 发送http post请求
            String strRespOnoff = HttpUtil.httpPostJson(hostPort + url, JSONObject.toJSONString(param));
            JSONObject object = JSONObject.parseObject(strRespOnoff);
            // 0 code表示成功
            if ("0".equals(object.getString("Ret"))) {
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
     * @description: 手动获取数据上传
     * @author: xwb
     * @param: [devID]
     * @return: void
     * @date: 2019/10/8 17:52
     */
    public void upLtDevData(String devID, String user, String passwd, String hostPort) {

        // 登录广播服务
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String jsessionid = login(user, passwd, hostPort);
        if (StringUtils.isEmpty(jsessionid)) {
            log.info("lt braodcast server login failed! ");
            return;
        }
        // 获取终端Id
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Integer> termIds = getTermIds(1, jsessionid, hostPort);
        if (termIds == null) {
            log.info("client is not exist !");
            return;
        }
        // 上传设备状态数据
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String payloadterms = getTermState(1, jsessionid, termIds, hostPort);
        // 获取节目ID
        JSONArray termStateArr = JSONArray.parseArray(payloadterms);
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < termStateArr.size(); i++) {
            Object o = termStateArr.get(i);
            JSONObject prg = JSONObject.parseObject(o.toString());
            String ip = prg.getString("IP");
            String status = prg.getString("Status");
            String name = prg.getString("Name");
            String Vol = prg.getString("Vol");
            param.put("devSn", devID);
            param.put("devType", 18);
            param.put("ip", ip);
            param.put("programfile", "");
            if (!StringUtils.isEmpty(status)) {
                int i1 = Integer.parseInt(status);
                param.put("status", i1);
                if (i1 == -1) {
                    param.put("online", 0);
                } else {
                    param.put("online", 1);
                }
            }
            param.put("updateTime", System.currentTimeMillis());
            param.put("userId", "");
            if (!StringUtils.isEmpty(Vol)) {
                param.put("volume", Integer.parseInt(Vol));
            }
            qjUpstreamService.analysisMessage(null, JSONObject.toJSONString(param));
            break;
        }
    }
}
