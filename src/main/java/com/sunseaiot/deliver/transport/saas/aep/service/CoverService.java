/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.service;
/**
 * aep井盖
 *
 * @Author: wanglong
 * @Date: 2019/12/31
 **/
public interface CoverService {
    /**
     * 设备数据变化通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    public String upDataPlayLoad(String str);

    /**
     * 设备指令响应通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    public String upInstructionsPlayLoad(String str);

    /**
     * 设备事件上报通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    public String upEventPlayLoad(String str);

    /**
     * 设备上下线通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    public String upOnlineOrOfflinePlayLoad(String str);

    /**
     * 设备增加删除通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    public String upAddOrDelPlayLoad(String str);
}
