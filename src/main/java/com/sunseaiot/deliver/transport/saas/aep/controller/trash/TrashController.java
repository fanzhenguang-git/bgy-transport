/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.controller.trash;

import com.sunseaiot.deliver.transport.saas.aep.service.TrashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * aep-垃圾桶
 *
 * @Author: wanglong
 * @Date: 2019/12/24
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1/http/aep/trash")
public class TrashController {

    @Autowired
    TrashService service;

    /**
     * 设备数据变化通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    @RequestMapping("/data/play/load")
    public String upDataPlayLoad(@RequestBody String str) {
        log.info("aep-垃圾桶设备数据变化通知: {}", str);
        return service.upDataPlayLoad(str);
    }

    /**
     * 设备指令响应通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    @RequestMapping("/instructions/play/load")
    public String upInstructionsPlayLoad(@RequestBody String str) {
        log.info("aep-垃圾桶设备指令响应通知: {}", str);
        return null;
    }

    /**
     * 设备事件上报通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    @RequestMapping("/event/play/load")
    public String upEventPlayLoad(@RequestBody String str) {
        log.info("aep-垃圾桶设备事件上报通知: {}", str);
        return service.upEventPlayLoad(str);
    }

    /**
     * 设备上下线通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    @RequestMapping("/online/play/load")
    public String upOnlineOrOfflinePlayLoad(@RequestBody String str) {
        log.info("aep-垃圾桶设备上下线通知: {}", str);
        return null;
    }

    /**
     * 设备增加删除通知
     *
     * @Author: wanglong
     * @Date: 2019/12/24
     **/
    @RequestMapping("/add/play/load")
    public String upAddOrDelPlayLoad(@RequestBody String str) {
        log.info("aep-垃圾桶设备增加删除通知: {}", str);
        return null;
    }
}