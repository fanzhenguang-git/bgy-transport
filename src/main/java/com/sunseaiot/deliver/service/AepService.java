/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service;

import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.sunseaiot.deliver.transport.saas.aep.vo.PostDataChangeVo;

/**
 * Aep平台服务
 *
 * @author hexing
 * @date 2019/12/25 10:28
 */
public interface AepService {

    /**
     * 创建指令客户端
     *
     * @return
     */
    AepDeviceCommandClient buildAepCommand();

    /**
     * 发送指令
     *
     * @param masterKey masterKey
     * @param bytes     指令
     * @return
     * @throws Exception
     */
     Object addInstruction(String masterKey, byte[] bytes) throws Exception;

    /**
     * 保存数据业务上报
     *
     * @param postDataChangeVo
     */
     void saveBusiness(PostDataChangeVo postDataChangeVo);

    /**
     * 保存事件上报
     *
     * @param postDataChangeVo
     */
     void saveEvent(PostDataChangeVo postDataChangeVo);
}
