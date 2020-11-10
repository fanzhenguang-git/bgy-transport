/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.controller.smoke;

import com.sunseaiot.deliver.transport.saas.aep.service.SmokeSAService;
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
@RequestMapping("aep/smoke_sa")
public class SmokeSAController {

    @Autowired
    @Qualifier("smokeSAService")
    private SmokeSAService smokeSAService;

    /**
     * AEP 获取业务数据
     *
     * @param postDataChangeVo
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "getBusiness")
    public void getHs2saBusiness(@RequestBody PostDataChangeVo postDataChangeVo, HttpServletRequest request, HttpServletResponse response) {
        log.info("烟感hs2sa业务上报数据变化数据：" + GsonUtil.toJson(postDataChangeVo));
        smokeSAService.saveBusiness(postDataChangeVo);
    }

    /**
     * AEP 获取事件上报数据
     *
     * @param postDataChangeVo
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "getEvent")
    public void getHs2saEvent(@RequestBody PostDataChangeVo postDataChangeVo, HttpServletRequest request, HttpServletResponse response){
        log.info("烟感hs2sa事件上报数据变化数据：" + GsonUtil.toJson(postDataChangeVo));
        smokeSAService.saveEvent(postDataChangeVo);
    }
}
