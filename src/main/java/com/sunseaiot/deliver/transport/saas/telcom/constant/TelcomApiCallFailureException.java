/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.telcom.constant;

/**
 * @author wangyongjun
 * @date 2019/5/13.
 */
public class TelcomApiCallFailureException extends Exception {

    public TelcomApiCallFailureException(String message) {
        super(message);
    }

    public TelcomApiCallFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
