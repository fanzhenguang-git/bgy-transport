/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.controller.smoke;

import com.sunseaiot.deliver.transport.saas.aep.service.SmokeSDService;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;
import com.sunseaiot.deliver.transport.saas.telcom.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能烟感hs2sa
 *
 * @author lua
 * @date 2019/12/28
 */
@Slf4j
@RestController
@RequestMapping("aep/smoke_sd")
public class SmokeSDController {

    @Autowired
    private SmokeSDService smokeSDService;

    /**
     * AEP 获取业务数据
     *
     * @param postDataChangeVo
     * @return
     */
    @PostMapping(value = "getBusiness")
    public void getBusiness(@RequestBody PostDataChangeVo postDataChangeVo) {
        log.info("烟感--RHWL-SD(JD-SD51）业务和事件上报数据变化数据：" + GsonUtil.toJson(postDataChangeVo));
        smokeSDService.saveBusiness(postDataChangeVo);
    }
}
