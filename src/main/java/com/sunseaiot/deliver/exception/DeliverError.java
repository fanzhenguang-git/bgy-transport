/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.exception;

/**
 * @author: Yanfeng Zhang
 * @date: 12/06/2019
 **/
public enum DeliverError {

    /**
     * 兼容grpc错误码
     */
    // 非法参数
    PARAMETER_INVALID(400, "Bad request message: [%s]"),

    // 非法操作
    ILLEGAL_OPERATION(403, "Illegal operation"),

    // 资源不存在
    RESOURCE_NOT_FOUND(404, "resource not found"),

    // 内部服务异常
    INTERNAL_SERVER_ERROR(500, "Internal server error"),

    // 服务不可用
    SERVICE_UNAVAILABLE(503, "Service unavailable"),

    // 参数缺失
    PARAMETER_MISSING(605, "Required parameters cannot be empty or incorrect format, parameter is [%s]"),

    /**
     * 给transport层用的错误码
     */
    // 设备不存在
    DEVICE_NOT_EXIST(100101, "device not exist"),

    // 通知服务不存在
    NOTIFICATION_SERVICE_NOT_EXIST(100102, "notofication service not exist"),

    
    /**
     * 对接AEP错误码
     */
    SYS_ERR_INTERNAL_SERVER_ERROR(9900, "内部错误，请联系管理员"),

    SYS_ERR_REQUIRED_PARAM_ABSENT(9901, "缺少请求参数"),

    SYS_ERR_READ_REQ_BODY_FAILED(9902, "参数解析失败"),

    SYS_ERR_INVALID_METHOD_PARAM(9903, "参数验证失败"),

    SYS_ERR_BIND_PARAM_FAILED(9904, "参数绑定失败"),

    SYS_ERR_METHOD_NOT_ALLOWED(9905, "不支持当前请求方法"),

    SYS_ERR_UNSUPPORTED_MEDIA_TYPE(9906, "不支持的媒体类型"),

    SYS_ERR_DATA_INTIGRETY_VIOLATION(9907, "操作数据库出现异常：字段重复、有外键关联等"),

    SYS_ERR_JDBC_CONNECTION_TIMEOUT(9908, "操作数据库出现连接异常"),

    PRODUCT_DATASET_NULL_INPUT(1201, "请求入参为空"),

    PRODUCT_DATASET_INVALID_NAME(1202, "name参数不合法，要求长度1-64，合法输入为：字母、数字、汉字及下划线"),

    RODUCT_DATASET_INVALID_DESC(1203, "description参数不合法，要求长度0-256，合法输入为：字母、数字、汉字空格、下划线及部分常见中文标点"),

    PRODUCT_DATASET_PRODUCT_ABSENT(1204, "产品数据集关联的产品不存在"),

    PRODUCT_DATASET_INVALID_TENANTID(1205, "产品数据集的租户ID与关联的产品不一致"),

    ;

    private int code;

    private String message;

    DeliverError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
