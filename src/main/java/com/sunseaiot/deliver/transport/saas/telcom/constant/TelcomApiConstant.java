/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.constant;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
public class TelcomApiConstant {

    public static final String TELCOM_API_ACCESSTOKEN_CACHE_NAME = "telcomAccessTokens";

    public static final String TELCOM_API_ACCESSTOKEN_CACHE_KEY_PREFIX = "apollo-telcom-adapter::accessToken::";

    /**
     * 实际为3600，比实际有效期短1分钟，以防止拿到过期token
     */
    public static final long TELCOM_API_ACCESSTOKEN_EXPIRE = 3540;

    public static final String TELCOM_API_TEST_ENV_HOST = "https://180.101.147.89:8743";

    public static final String TELCOM_API_TEST_ENV_CERT_FILE_NAME = "TelcomTestEnvApiServerCertificate.jks";

    public static final String TELCOM_API_TEST_ENV_CERT_FILE_PASS = "123456";

    public static final String TELCOM_API_TEST_ENV_CERT_SIGN = "B16142BE477FEABBC825EBF8B19067E4E3D130E2";

    public static final String TELCOM_API_PROD_ENV_HOST = "https://180.101.149.140:8743";

    public static final String TELCOM_API_PROD_ENV_CERT_FILE_NAME = "TelcomProdEnvApiServerCertificate.jks";

    public static final String TELCOM_API_PROD_ENV_CERT_FILE_PASS = "SunseaIoT2019!";

    public static final String TELCOM_API_PROD_ENV_CERT_SIGN = "0FBC8107CEA1667B95DF1719C77E4BD185971D22";

    public static final String TELCOM_API_LOGIN_URL = "/iocm/app/sec/v1.1.0/login";

    public static final String TELCOM_API_CREATE_DEVICE_COMMAND = "/iocm/app/cmd/v1.4.0/deviceCommands";

    public static final String TELCOM_API_SUBSCRIBE_BUSINESS_DATA = "/iocm/app/sub/v1.2.0/subscriptions";

    public static final String TELCOM_API_DELETE_SUBSCRIPTIONS = "/iocm/app/sub/v1.2.0/subscriptions";

    public static final String TELCOM_API_QUERY_DEVICE = "/iocm/app/dm/v1.4.0/devices/{deviceId}";

    public static final String CALL_BACK_URL_PREFIX = "/api/v1/telcom/hook/";

    public enum NotifyType {

        /**
         * 设备信息变化通知
         */
        deviceInfoChanged,

        /**
         * 设备数据批量变化通知
         */
        deviceDatasChanged
    }

    public enum DeviceStatus {

        /**
         *
         */
        ONLINE,

        /**
         * 离线
         */
        OFFLINE,

        /**
         * 异常
         */
        ABNORMAL
    }

    /**
     * 电信NB平台北向对接环境枚举
     */
    public enum TelcomEnv {

        /**
         * 开发测试环境
         */
        test,

        /**
         * 生产环境
         */
        prod
    }
}
