/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.broadcasttask;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 任务每周设置
 *
 * @author: xwb
 * @create: 2019/09/29 19:45
 */
public class EveryWeek {

    /**
     * 代表每几周  任务每几周执行（每隔every-1周执行）
     */
    @JSONField(name = "Every")
    private Integer Every;

    /**
     * 一周中的有效天 周一到周日 (1-7)
     */
    private List<Integer> DaysInWeek;

    public Integer getEvery() {
        return Every;
    }

    public void setEvery(Integer every) {
        Every = every;
    }

    public List<Integer> getDaysInWeek() {
        return DaysInWeek;
    }

    public void setDaysInWeek(List<Integer> daysInWeek) {
        DaysInWeek = daysInWeek;
    }
}
