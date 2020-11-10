/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji;

import lombok.Data;

/**
 * 广播模块-下发定时任务-任务详情数组对象
 *
 * @author: XJP
 * @create: 2019/08/16 17:12
 */
@Data
public class BroadcastTaskItem {

    /**
     * 开始时间(HH:mm:ss)
     */
    private String startTime;

    /**
     * 结束时间(HH:mm:ss)
     */
    private String endTime;

    /**
     * 文件ID(多个文件以逗号分隔)
     */
    private String fileIds;

    /**
     * 终端ID(多个设备以逗号分隔)
     */
    private String terminalIds;

    /**
     * 终端ID(多个设备以逗号分隔)
     */
    private int playModel;
}
