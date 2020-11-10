/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.controller.geo;

import com.sunseaiot.deliver.transport.saas.aep.service.LightService;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;
import com.sunseaiot.deliver.transport.saas.telcom.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 灯控controller
 *
 * @author hexing
 * @date 2020/1/17 10:32
 */
@Slf4j
@RestController
@RequestMapping("aep")
public class LightController {

    @Autowired
    private LightService lightService;

    @PostMapping(value = "getBusiness")
    public void getBusiness(@RequestBody PostDataChangeVo postDataChangeVo) {
        log.info(GsonUtil.toJson(postDataChangeVo));
        lightService.saveBusiness(postDataChangeVo);
    }
}
