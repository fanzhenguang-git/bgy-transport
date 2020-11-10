/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.service;

import com.sunseaiot.deliver.transport.saas.telcom.constant.TelcomApiCallFailureException;
import com.sunseaiot.deliver.transport.saas.telcom.dto.*;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
public interface TelcomApiService {

    /**
     * 创建设备命令
     *
     * @param appId   设备appId
     * @param request 创建设备命令请求对象
     * @return 创建设备命令响应
     * @throws TelcomApiCallFailureException 调用电信NB平台失败异常
     */
    CreateDeviceCommandResponse createDeviceCommand(String appId, CreateDeviceCommandRequest request) throws TelcomApiCallFailureException;

    /**
     * 订阅业务数据
     *
     * @param appId   设备appId
     * @param request 订阅业务数据请求对象
     * @return 订阅业务数据响应
     * @throws TelcomApiCallFailureException 调用电信NB平台失败异常
     */
    SubscribeBusinessDataResponse subscribeBusinessData(String appId, SubscribeBusinessDataRequest request) throws TelcomApiCallFailureException;

    /**
     * 查询设备详情
     *
     * @param appId   设备appId
     * @param request 查询设备详情请求对象
     * @return 查询设备详情响应
     * @throws TelcomApiCallFailureException 调用电信NB平台失败异常
     */
    QueryDeviceResponse queryDevice(String appId, QueryDeviceRequest request) throws TelcomApiCallFailureException;

    /**
     * 取消订阅
     * 只传appId，删除该app的所有订阅
     * 传appId+notifyType，删除该app的指定notifyType的订阅
     * 传appId+callbackUrl，删除该app的指定callbackUrl的订阅
     * 传appId+callbackUrl+notifyType，删除该app的指定callbackUrl且为指定notifyType的订阅
     *
     * @param appId       必传
     * @param callbackUrl 可选
     * @param notifyType  可选
     * @throws TelcomApiCallFailureException 调用电信NB平台失败异常
     */
    void unsubscribe(String appId, String callbackUrl, String notifyType) throws TelcomApiCallFailureException;
}
