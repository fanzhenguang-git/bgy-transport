/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.grpc;

import com.sunseaiot.deliver.dto.AttributeKVTs;
import com.sunseaiot.deliver.dto.TypeValue;
import com.sunseaiot.deliver.exception.DeliverException;
import com.sunseaiot.deliver.rpc.basedata.KeyValueProto;
import com.sunseaiot.deliver.rpc.basedata.KeyValueType;
import com.sunseaiot.deliver.rpc.device.*;
import com.sunseaiot.deliver.rpc.device.DeviceServiceGrpc.DeviceServiceImplBase;
import com.sunseaiot.deliver.rpc.status.Code;
import com.sunseaiot.deliver.rpc.status.Status;
import com.sunseaiot.deliver.service.DeviceDataService;
import com.sunseaiot.deliver.service.DownstreamService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GrpcDeviceService extends DeviceServiceImplBase {

    @Autowired
    private DownstreamService downstreamService;

    @Autowired
    private DeviceDataService deviceDataService;

    @Override
    public void getDeviceData(GetDeviceDataRequest request, StreamObserver<GetDeviceDataResponse> responseObserver) {

        long startTime = System.currentTimeMillis();

        GetDeviceDataResponse.Builder responseBuilder = GetDeviceDataResponse.newBuilder();
        try {
            request.getDeviceIdList().forEach(id -> {
                log.debug("deviceId: {}, time: {}", id, System.currentTimeMillis());
                DeviceData.Builder deviceDataBuilder = DeviceData.newBuilder();
                deviceDataBuilder.setDeviceId(id);
                deviceDataBuilder.setDeviceStatus(deviceDataService.getDeviceStatus(id));
                List<KeyValueProto> protoList = deviceDataService.getAllLatestAttribute(id);
                deviceDataBuilder.addAllAttributeValue(protoList);
                responseBuilder.addDeviceData(deviceDataBuilder.build());
            });
            responseBuilder.setStatus(getSuccessStatus());
        } catch (DeliverException e1) {
            log.error("DeliverException getDeviceUploadData: {}, {}, {}", e1.getErrorCode(), e1.getErrorMessage(),
                    e1.getStackTrace());
            responseBuilder.setStatus(Status.newBuilder().setCode(e1.getErrorCode()).setMessage(e1.getMessage()).build());
        } catch (Exception e2) {
            log.error("unknow Exception getDeviceUploadData: {}, {}", e2.getMessage(), e2.getStackTrace());
            responseBuilder.setStatus(Status.newBuilder().setCode(Code.SC_INTERNAL_SERVER_ERROR_VALUE).build());
        }
        log.info("getDeviceUploadData, time cost: {}", System.currentTimeMillis() - startTime);
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getDeviceHistoryData(GetDeviceHistoryDataRequest request,
                                     StreamObserver<GetDeviceHistoryDataResponse> responseObserver) {

        log.info("getDeviceHistoryData: {}", System.currentTimeMillis());
        String deviceId = request.getDeviceId();
        String key = request.getAttributeKey();
        long start = request.getStartTime();
        long stop = request.getStopTime();
        GetDeviceHistoryDataResponse.Builder responseBuilder = GetDeviceHistoryDataResponse.newBuilder();
        responseBuilder.setDeviceId(deviceId);
        try {
            List<KeyValueProto> attributeList = deviceDataService.getDeviceHistoryAttribute(deviceId, key, start, stop);
            responseBuilder.addAllAttributeValue(attributeList);
            responseBuilder.setStatus(getSuccessStatus());
        } catch (DeliverException e1) {
            log.error("DeliverException getDeviceHistoryData: {}, {}", e1.getErrorCode(), e1.getErrorMessage());
            responseBuilder.setStatus(Status.newBuilder().setCode(e1.getErrorCode()).setMessage(e1.getMessage()).build());
        } catch (Exception e2) {
            log.error("unknow Exception getDeviceHistoryData: {}, {}", e2.getMessage(), e2.getStackTrace());
            responseBuilder.setStatus(Status.newBuilder().setCode(Code.SC_INTERNAL_SERVER_ERROR_VALUE).build());
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void pushDownData(PushDownDataRequest request, StreamObserver<PushDownDataResponse> responseObserver) {
        log.debug("pubDeviceDownloadData");
        PushDownDataResponse.Builder responseBuilder = PushDownDataResponse.newBuilder();
        String deviceId = request.getDeviceId();

        Map<String, TypeValue> attributeMap = new HashMap<>();
        request.getPubAttributeListList().forEach(keyValueProto -> {
            String key = keyValueProto.getKey();
            TypeValue value = protoToAttributeValue(keyValueProto);
            attributeMap.put(key, value);
        });
        try {
            downstreamService.sendData(deviceId, attributeMap);
            responseBuilder.setStatus(getSuccessStatus());
        } catch (DeliverException e1) {
            log.error("DeliverException pubDeviceDownloadData: {}, {}", e1.getErrorCode(), e1.getErrorMessage());
            responseBuilder.setStatus(Status.newBuilder().setCode(e1.getErrorCode()).setMessage(e1.getMessage()).build());
        } catch (Exception e2) {
            log.error("unknow Exception pubDeviceDownloadData: {}, {}", e2.getMessage(), e2.getStackTrace());
            responseBuilder.setStatus(Status.newBuilder().setCode(Code.SC_INTERNAL_SERVER_ERROR_VALUE).build());
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void pushDeviceAction(PushDeviceActionRequest request,
                                 StreamObserver<PushDeviceActionResponse> responseObserver) {

        log.debug("pubDeviceAction");
        PushDeviceActionResponse.Builder responseBuilder = PushDeviceActionResponse.newBuilder();
        String deviceId = request.getDeviceId();
        String actionName = request.getActionName();
        Map<String, TypeValue> parameterMap = new HashMap<>();
        request.getParameterList().forEach(keyValueProto -> {
            String key = keyValueProto.getKey();
            TypeValue value = protoToAttributeValue(keyValueProto);
            parameterMap.put(key, value);
        });
        try {
            downstreamService.sendAction(deviceId, actionName, parameterMap);
            responseBuilder.setStatus(getSuccessStatus());
        } catch (DeliverException e1) {
            log.error("DeliverException pubDeviceAction: {}, {}", e1.getErrorCode(), e1.getErrorMessage());
            responseBuilder.setStatus(Status.newBuilder().setCode(e1.getErrorCode()).setMessage(e1.getMessage()).build());
        } catch (Exception e2) {
            log.error("unknow Exception pubDeviceAction: {}, {}", e2.getMessage(), e2.getStackTrace());
            responseBuilder.setStatus(Status.newBuilder().setCode(Code.SC_INTERNAL_SERVER_ERROR_VALUE).build());
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    private KeyValueProto attributeValueToProto(AttributeKVTs kVTs) {

        KeyValueProto.Builder builder = KeyValueProto.newBuilder();
        builder.setKey(kVTs.getKey());
        builder.setKeyName(kVTs.getKeyName());
        builder.setTimestamp(kVTs.getTimestamp());
        TypeValue value = kVTs.getTypeValue();
        switch (value.getValueCase()) {
            case BOOLVALUE:
                builder.setType(KeyValueType.BOOLEAN_V);
                builder.setBoolValue(value.getBoolValue());
                break;
            case INTVALUE:
                builder.setType(KeyValueType.LONG_V);
                builder.setLongValue(value.getIntValue());
                break;
            case DOUBLEVALUE:
                builder.setType(KeyValueType.DOUBLE_V);
                builder.setDoubleValue(value.getDoubleValue());
                break;
            case STRINGVALUE:
                builder.setType(KeyValueType.STRING_V);
                builder.setStringValue(value.getStringValue());
                break;
            case OBJECTVALUE:
                builder.setType(KeyValueType.OBJECT_V);
                builder.setStringValue(value.getObjectValue());
                break;
            default:
                log.warn("type error");
                break;
        }
        return builder.build();
    }

    private TypeValue protoToAttributeValue(KeyValueProto keyValueProto) {
        
        TypeValue typeValue = new TypeValue();
        switch (keyValueProto.getType()) {
            case BOOLEAN_V:
                typeValue.setBoolValue(keyValueProto.getBoolValue());
                break;
            case LONG_V:
                typeValue.setIntValue(keyValueProto.getLongValue());
                break;
            case DOUBLE_V:
                typeValue.setDoubleValue(keyValueProto.getDoubleValue());
                break;
            case STRING_V:
                typeValue.setStringValue(keyValueProto.getStringValue());
                break;
            default:
                log.error("type error");
                break;
        }
        return typeValue;
    }

    private Status getSuccessStatus() {
        return Status.newBuilder().setCode(Code.SC_OK_VALUE).setMessage("success").build();
    }
}
