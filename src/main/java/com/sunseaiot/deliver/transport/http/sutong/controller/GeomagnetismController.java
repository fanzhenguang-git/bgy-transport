/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.http.sutong.controller;

import com.sunseaiot.deliver.transport.http.sutong.service.GeomagnetismService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地磁数据入口
 *
 * @author jyl
 * @date 2019-09-06
 */
@RestController
public class GeomagnetismController {

    @Autowired
    GeomagnetismService service;

    @RequestMapping("/api/v1/http/st")
    public String upPlayLoad(@RequestBody String str) {
        return service.upPlayLoad(str);
    }
}