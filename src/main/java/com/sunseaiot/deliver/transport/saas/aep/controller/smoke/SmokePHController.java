/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.controller.smoke;

import com.sunseaiot.deliver.transport.saas.aep.service.SmokePHService;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;
import com.sunseaiot.deliver.transport.saas.telcom.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 智能烟感hs2sa
 *
 * @author lua
 * @date 2019/12/28
 */
@Slf4j
@RestController
@RequestMapping("aep/smoke_ph")
public class SmokePHController {

    @Autowired
    @Qualifier("smokePHService")
    private SmokePHService smokePHService;

    /**
     * AEP 获取业务数据
     *
     * @param postDataChangeVo
     * @param response
     * @param request
     * @return
     */
    @PostMapping(value = "getBusiness")
    public void get633PHBusiness(@RequestBody PostDataChangeVo postDataChangeVo, HttpServletRequest request, HttpServletResponse response) {
        log.info("烟感633PH业务上报数据变化数据：" + GsonUtil.toJson(postDataChangeVo));
        smokePHService.saveBusiness(postDataChangeVo);
    }

    /**
     * AEP 获取事件上报数据
     *
     * @param postDataChangeVo
     * @param response
     * @param request
     * @return
     */
    @PostMapping(value = "getEvent")
    public void get633PHEvent(@RequestBody PostDataChangeVo postDataChangeVo, HttpServletRequest request, HttpServletResponse response){
        log.info("烟感633PH事件上报数据变化数据：" + GsonUtil.toJson(postDataChangeVo));
        smokePHService.saveEvent(postDataChangeVo);
    }
}
