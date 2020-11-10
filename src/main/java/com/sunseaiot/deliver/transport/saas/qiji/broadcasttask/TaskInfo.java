/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.broadcasttask;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 广播任务
 *
 * @author: xwb
 * @date: 2019/9/29 19:25
 */
public class TaskInfo {

    /**
     * 任务ID -1代表新建 必填
     */
    @JSONField(name = "ID")
    private Integer ID;

    /**
     * 名称 必填
     */
    @JSONField(name = "Name")
    private String Name;

    /**
     * 任务类型 1-每天任务，2-每周任务，3-每月任务，4-一次性任务 必填，选什么类型，什么类型必填
     */
    @JSONField(name = "Type")
    private Integer Type;

    /**
     * 每天任务参数
     */
    @JSONField(name = "DayItem")
    private EveryDay DayItem;

    /**
     * 每周任务参数
     */
    @JSONField(name = "WeekItem")
    private EveryWeek WeekItem;

    /**
     * 每月任务参数
     */
    @JSONField(name = "MonthItem")
    private EveryMonth MonthItem;

    /**
     * 起始时间 yyyy-mm-dd hh:mm:ss 必填
     */
    @JSONField(name = "StartTime")
    private String StartTime;

    /**
     * 截止日期 yyyy-mm-dd
     */
    @JSONField(name = "EndDate")
    private String EndDate;

    /**
     * 播放模式 0-顺序，1-随机 必填
     */
    @JSONField(name = "PlayMode")
    private Integer PlayMode;

    /**
     * 节目列表 必填
     */
    @JSONField(name = "ProgIds")
    private List<Integer> ProgIds;

    /**
     * 终端列表 必填
     */
    @JSONField(name = "Tids")
    private List<Integer> Tids;

    /**
     * 持续时间，任务时长 单位秒，和 RepeatTime 之间有且只有一个
     */
    @JSONField(name = "Length")
    private Integer Length;

    /**
     * 播放节目列表次数 和 Length 之间有且只有一个
     */
    @JSONField(name = "RepeatTime")
    private Integer RepeatTime;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public EveryDay getDayItem() {
        return DayItem;
    }

    public void setDayItem(EveryDay dayItem) {
        DayItem = dayItem;
    }

    public EveryWeek getWeekItem() {
        return WeekItem;
    }

    public void setWeekItem(EveryWeek weekItem) {
        WeekItem = weekItem;
    }

    public EveryMonth getMonthItem() {
        return MonthItem;
    }

    public void setMonthItem(EveryMonth monthItem) {
        MonthItem = monthItem;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public Integer getPlayMode() {
        return PlayMode;
    }

    public void setPlayMode(Integer playMode) {
        PlayMode = playMode;
    }

    public List<Integer> getProgIds() {
        return ProgIds;
    }

    public void setProgIds(List<Integer> progIds) {
        ProgIds = progIds;
    }

    public List<Integer> getTids() {
        return Tids;
    }

    public void setTids(List<Integer> tids) {
        Tids = tids;
    }

    public Integer getLength() {
        return Length;
    }

    public void setLength(Integer length) {
        Length = length;
    }

    public Integer getRepeatTime() {
        return RepeatTime;
    }

    public void setRepeatTime(Integer repeatTime) {
        RepeatTime = repeatTime;
    }
}
