/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.aep.controller.geo;

import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.transport.saas.aep.service.GeoService;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;
import com.sunseaiot.deliver.transport.saas.telcom.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 地磁控制类
 *
 * @author hexing
 * @date 2019/12/25 14:44
 */
@Slf4j
@RestController
@RequestMapping("aep/geo")
public class GeoController {
    @Autowired
    private GeoService geoService;

    /**
     * AEP 获取业务数据
     *
     * @param postDataChangeVo
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "getBusiness")
    public void getBusiness(@RequestBody PostDataChangeVo postDataChangeVo, HttpServletRequest request, HttpServletResponse response) {
        // Map<String, Object> requestParam = getAllRequestParam(request);
        log.info("地磁上报数据变化数据：" + GsonUtil.toJson(postDataChangeVo));
        geoService.saveBusiness(postDataChangeVo);
    }

    /**
     * 获取客户端请求参数中所有的信息
     *
     * @param request
     * @return
     */
    private Map<String, Object> getAllRequestParam(final HttpServletRequest request) {
        Map<String, Object> res = new HashMap<>(16);
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                Object value = request.getParameter(en);
                if (value instanceof Boolean) {
                    res.put(en, new TypeValue().setBoolValue((boolean) value));
                } else if (value instanceof Long) {
                    res.put(en, new TypeValue().setIntValue((long) value));
                } else if (value instanceof Double) {
                    res.put(en, new TypeValue().setDoubleValue((double) value));
                } else if (value instanceof String) {
                    res.put(en, new TypeValue().setStringValue((String) value));
                }

                /*//如果字段的值为空，判断若值为空，则删除这个字段>
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }*/
            }
        }
        return res;
    }
}
