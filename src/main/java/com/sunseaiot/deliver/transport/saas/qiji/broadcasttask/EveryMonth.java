/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.broadcasttask;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 每月执行
 *
 * @author: xwb
 * @create: 2019/09/29 19:49
 */
public class EveryMonth {

    /**
     * 一年中的有效月份 月序号(1-12)
     */
    @JSONField(name = "MonthsInYear")
    private List<Integer> MonthsInYear;

    /**
     * 代表每月中的哪 一天 数组大小为1或2
     * 数组大小:
     * =1 时表示每月中的第几天取值:1-31
     * =2 时表示每月中的第几个星期几，第一个数据取值1 - 第一个,
     * 2 - 第二个,3 - 第三个,4 - 第四个,5 – 最后一个,
     * 第二个数据取值 1-7
     */
    @JSONField(name = "DayInMonth")
    private List<Integer> DayInMonth;

    public List<Integer> getMonthsInYear() {
        return MonthsInYear;
    }

    public void setMonthsInYear(List<Integer> monthsInYear) {
        MonthsInYear = monthsInYear;
    }

    public List<Integer> getDayInMonth() {
        return DayInMonth;
    }

    public void setDayInMonth(List<Integer> dayInMonth) {
        DayInMonth = dayInMonth;
    }
}
