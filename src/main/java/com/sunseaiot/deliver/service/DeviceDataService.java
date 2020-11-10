/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.service;

import com.sunseaiot.deliver.rpc.basedata.KeyValueProto;
import com.sunseaiot.deliver.rpc.device.DeviceStatus;

import java.util.List;

/**
 * @author hupan
 * @date 2019-08-15.
 */
public interface DeviceDataService {

    DeviceStatus getDeviceStatus(String deviceId);

    List<KeyValueProto> getAllLatestAttribute(String deviceId);

    List<KeyValueProto> getDeviceHistoryAttribute(String deviceId, String attributeKey, long startTime, long stopTime);
}
