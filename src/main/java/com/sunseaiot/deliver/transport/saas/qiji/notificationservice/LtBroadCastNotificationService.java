/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.notificationservice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.dao.DeviceDao;
import com.sunseaiot.deliver.dao.TransportConfigDao;
import com.sunseaiot.deliver.dao.entity.DeviceEntity;
import com.sunseaiot.deliver.dao.entity.TransportConfigEntity;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.service.NotificationService;
import com.sunseaiot.deliver.service.RegisterService;
import com.sunseaiot.deliver.transport.saas.qiji.BroadCastDirectControl;
import com.sunseaiot.deliver.transport.saas.qiji.broadcasttask.EveryDay;
import com.sunseaiot.deliver.transport.saas.qiji.broadcasttask.EveryMonth;
import com.sunseaiot.deliver.transport.saas.qiji.broadcasttask.EveryWeek;
import com.sunseaiot.deliver.transport.saas.qiji.broadcasttask.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 雷拓广播直接调用下行通知服务
 *
 * @author: xwb
 * @date: 2019/10/8 15:58
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "deliver.transport.qj.enable", havingValue = "true")
public class LtBroadCastNotificationService implements NotificationService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private BroadCastDirectControl broadcast;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private TransportConfigDao transportConfigDao;

    @Value("${deliver.product-id.lt-broadcast}")
    private String productId;

    @PostConstruct
    public void init() {

        String serviceId = registerService.registerNotification(this);
        List<DeviceEntity> deviceEntitylist = deviceDao.findByProductId(productId);
        deviceEntitylist.stream().forEach(deviceEntity -> registerService.registerDevice(serviceId, deviceEntity.getId()));
    }

    @Override
    public void onOriginData(String deviceId, String payload) {

    }

    @Override
    public void onMessage(String deviceId, Map<String, TypeValue> attributeMap) {

    }

    @Override
    public void onAction(String deviceId, String actionName, Map<String, TypeValue> parameterMap) {

        // 查询配置表
        TransportConfigEntity byDeviceId = transportConfigDao.findByDeviceId(deviceId);
        if (byDeviceId == null) {
            log.info("devSn is not exist");
            return;
        }
        // 获取参数
        JSONObject configObject = JSONObject.parseObject(byDeviceId.getConfigValue());
        String hostPort = configObject.getString("hostPort");
        String user = configObject.getString("user");
        String passwd = configObject.getString("passwd");
        String devSn = configObject.getString("devSn");
        // 登录广播服务
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String jsessionid = broadcast.login(user, passwd, hostPort);
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
        List<Integer> termIds = broadcast.getTermIds(1, jsessionid, hostPort);
        if (termIds == null) {
            log.info("client is not exist !");
            return;
        }
        // 设置音量
        if (BroadCastDirectControl.SET_VOLUME.equals(actionName)) {
            TypeValue volume = parameterMap.get("volume");
            if (volume == null || volume.getIntValue() == null) {
                log.info("volume is not correct!");
                return;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcast.setTermVol(1, jsessionid, termIds, volume.getIntValue().intValue(), hostPort);
        } else if (BroadCastDirectControl.UPLOAD_PROGRAME.equals(actionName)) {
            TypeValue orgFileName = parameterMap.get("fileName");
            if (orgFileName == null || StringUtils.isBlank(orgFileName.getStringValue())) {
                log.info("filePath is not correct!");
                return;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcast.uploadFile(orgFileName.getStringValue(), jsessionid, hostPort);
        } else if (BroadCastDirectControl.PLAY_PROGRAME.equals(actionName)) {
            TypeValue fileName = parameterMap.get("fileName");
            if (fileName == null || StringUtils.isBlank(fileName.getStringValue())) {
                log.info("fileName is not correct!");
                return;
            }
            // 获取节目id
            Integer programId = getProgramId(jsessionid, fileName, hostPort);
            // 创建会话
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String sid = broadcast.FileSessionCreate(termIds, 200, "testprg", jsessionid, hostPort);
            if (sid == null) {
                log.info("FileSessionCreate is failed !");
                return;
            }
            // 播放节目
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcast.FileSessionSetProg(Integer.parseInt(sid), programId, jsessionid, hostPort);
        } else if (BroadCastDirectControl.STOP_PALY_PROGRAME.equals(actionName)) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String termState = broadcast.getTermState(1, jsessionid, termIds, hostPort);
            // 获取节目ID
            JSONArray termStateArray = JSONArray.parseArray(termState);
            String sid = null;
            for (int i = 0; i < termStateArray.size(); i++) {
                Object o = termStateArray.get(i);
                JSONObject prg = JSONObject.parseObject(o.toString());
                sid = prg.getString("Sid");
                break;
            }
            if (sid == null) {
                log.info("FileSession is not exist !");
                return;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcast.FileSessionSetStat(Integer.parseInt(sid), 0, jsessionid, hostPort);
        } else if (BroadCastDirectControl.TASK_CREATE.equals(actionName)) {
            TypeValue taskName = parameterMap.get("taskName");
            if (taskName == null || StringUtils.isBlank(taskName.getStringValue())) {
                log.info("taskName is not correct!");
                return;
            }
            TypeValue type = parameterMap.get("type");
            if (type == null || type.getIntValue() == null) {
                log.info("type is not correct!");
                return;
            }
            TypeValue startTime = parameterMap.get("startTime");
            if (startTime == null || StringUtils.isBlank(startTime.getStringValue())) {
                log.info("type is not correct!");
                return;
            }
            TypeValue playMode = parameterMap.get("playMode");
            if (playMode == null || playMode.getIntValue() == null) {
                log.info("playMode is not correct!");
                return;
            }

            TypeValue endDate = parameterMap.get("endDate");
            TaskInfo taskInfo = new TaskInfo();
            // -1 代表新建任务
            taskInfo.setID(-1);
            // 任务名称s
            taskInfo.setName(taskName.getStringValue());
            // 1 –每天任务 2 – 每周任务 3 – 每月任务 4-一次性任务
            taskInfo.setType(type.getIntValue().intValue());
            TypeValue every = parameterMap.get("every");
            switch (type.getIntValue().intValue()) {
                case 1:
                    if (every == null || every.getIntValue() == null) {
                        log.info("every is not correct!");
                        break;
                    }
                    EveryDay everyDay = new EveryDay();
                    everyDay.setEvery(every.getIntValue().intValue());
                    taskInfo.setDayItem(everyDay);
                    break;
                case 2:
                    if (every == null || every.getIntValue() == null) {
                        log.info("every is not correct!");
                        break;
                    }
                    TypeValue daysInWeek = parameterMap.get("daysInWeek");
                    EveryWeek everyWeek = new EveryWeek();
                    everyWeek.setEvery(every.getIntValue().intValue());
                    // 一周中的有效天 周一到周日 (1-7) 整型数组
                    if (daysInWeek != null && StringUtils.isNotBlank(daysInWeek.getStringValue())) {
                        List<Integer> days = new ArrayList<>();
                        String[] split = daysInWeek.getStringValue().split(",");
                        for (int i = 0; i < split.length; i++) {
                            days.add(Integer.parseInt(split[i]));
                        }
                        everyWeek.setDaysInWeek(days);
                    }
                    taskInfo.setWeekItem(everyWeek);
                    break;
                case 3:
                    TypeValue monthsInYear = parameterMap.get("monthsInYear");
                    if (monthsInYear == null || StringUtils.isBlank(monthsInYear.getStringValue())) {
                        log.info("monthsInYear is not correct!");
                        break;
                    }
                    TypeValue dayInMonth = parameterMap.get("dayInMonth");
                    if (dayInMonth == null || StringUtils.isBlank(dayInMonth.getStringValue())) {
                        log.info("dayInMonth is not correct!");
                        break;
                    }
                    EveryMonth everyMonth = new EveryMonth();
                    // 整型数组 一年中的有效月份 月序号(1-12)
                    List<Integer> month = new ArrayList<>();
                    String[] split = monthsInYear.getStringValue().split(",");
                    for (int i = 0; i < split.length; i++) {
                        month.add(Integer.parseInt(split[i]));
                    }
                    everyMonth.setMonthsInYear(month);
                    /**整型数组代表每月中的哪 一天 数组大小为1或2,数组大小:=1 时表示每月中的第几天 取值:1-31 ,
                     =2 时表示每月中的第几个星 期几，
                     第一个数据取值1 - 第一个,2 - 第二个,3 - 第三个,4 - 第四个,5 – 最后一个 第二个数据取值 1-7
                     */
                    List<Integer> dmonth = new ArrayList<>();
                    String[] split2 = dayInMonth.getStringValue().split(",");
                    for (int i = 0; i < split2.length; i++) {
                        dmonth.add(Integer.parseInt(split2[i]));
                    }
                    everyMonth.setDayInMonth(dmonth);
                    taskInfo.setMonthItem(everyMonth);
                    break;
                default:
                    break;
            }
            // 起始时间yyyy-mm-dd hh:mm:ss
            taskInfo.setStartTime(startTime.getStringValue());
            // 截止日期 yyyy-mm-dd，可以没有本字段
            if (endDate != null && StringUtils.isNotBlank(endDate.getStringValue())) {
                taskInfo.setEndDate(endDate.getStringValue());
            }
            // 0-顺序 1-随机
            taskInfo.setPlayMode(playMode.getIntValue().intValue());
            // 节目名称
            TypeValue fileName = parameterMap.get("fileName");
            if (fileName == null || StringUtils.isBlank(fileName.getStringValue())) {
                log.info("fileName is not correct!");
                return;
            }
            // 任务时长，单位秒
            TypeValue length = parameterMap.get("length");
            // 和 Length 之间有且只有一个
            TypeValue repeatTime = parameterMap.get("repeatTime");
            if (length != null && repeatTime == null) {
                taskInfo.setLength(length.getIntValue().intValue());
            }
            // 播放节目列表次数
            if (repeatTime != null && length == null) {
                taskInfo.setRepeatTime(repeatTime.getIntValue().intValue());
            }
            // 获取节目id
            Integer programId = getProgramId(jsessionid, fileName, hostPort);
            List<Integer> progIds = new ArrayList<>();
            progIds.add(programId);
            taskInfo.setProgIds(progIds);
            // 终端ID
            taskInfo.setTids(termIds);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcast.taskCreate(jsessionid, taskInfo, hostPort);
        } else if (BroadCastDirectControl.TASK_DELETE.equals(actionName)) {
            TypeValue taskName = parameterMap.get("taskName");
            if (taskName == null || StringUtils.isBlank(taskName.getStringValue())) {
                log.info("taskName is not correct!");
                return;
            }
            // 获取任务ID
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Integer taskid = broadcast.taskList(jsessionid, taskName.getStringValue(), hostPort);
            // 执行删除
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcast.taskDelete(jsessionid, taskid, hostPort);
        } else if (BroadCastDirectControl.TASK_MANUAL_START.equals(actionName)) {
            TypeValue taskName = parameterMap.get("taskName");
            if (taskName == null || StringUtils.isBlank(taskName.getStringValue())) {
                log.info("taskName is not correct!");
                return;
            }
            // 获取任务ID
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Integer taskid = broadcast.taskList(jsessionid, taskName.getStringValue(), hostPort);
            // 启动任务
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcast.TaskManualStart(jsessionid, taskid, hostPort);
        } else if (BroadCastDirectControl.TASK_MANUAL_STOP.equals(actionName)) {
            TypeValue taskName = parameterMap.get("taskName");
            if (taskName == null || StringUtils.isBlank(taskName.getStringValue())) {
                log.info("taskName is not correct!");
                return;
            }
            // 获取任务ID
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Integer taskid = broadcast.taskList(jsessionid, taskName.getStringValue(), hostPort);
            // 启动任务
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcast.TaskManualStop(jsessionid, taskid, hostPort);
        }
    }

    private Integer getProgramId(String jsessionid, TypeValue fileName, String hostPort) {
        Integer prgId = null;
        // 获取节目ID
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String prgs = broadcast.MLListDir(jsessionid, hostPort);
        if (prgs == null) {
            log.info("programs is empty !");
            return null;
        }
        // 获取节目ID
        JSONArray jsonArray = JSONArray.parseArray(prgs);
        if (jsonArray.size() == 0) {
            log.info("programs is empty !");
            return null;
        }
        for (int j = 0; j < jsonArray.size(); j++) {
            Object o = jsonArray.get(j);

            JSONObject prg = JSONObject.parseObject(o.toString());
            if (fileName.getStringValue().equals(prg.getString("Name"))) {
                String id = prg.getString("ID");
                prgId = Integer.parseInt(id);
                break;
            }
        }
        return prgId;
    }
}
