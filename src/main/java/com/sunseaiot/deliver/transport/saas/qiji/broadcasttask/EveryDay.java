/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.broadcasttask;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 天数
 *
 * @author: xwb
 * @create: 2019/09/29 19:43
 */
public class EveryDay {

    /**
     * 代表每几天  任务每几天执行（每隔every-1天执行）
     */
    @JSONField(name = "Every")
    private Integer Every;

    public Integer getEvery() {
        return Every;
    }

    public void setEvery(Integer every) {
        Every = every;
    }
}
