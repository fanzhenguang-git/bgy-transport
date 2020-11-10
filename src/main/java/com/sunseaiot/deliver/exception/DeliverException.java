/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.exception;

import lombok.Getter;

public class DeliverException extends RuntimeException {

    @Getter
    protected int errorCode = 500;

    @Getter
    protected String errorMessage;

    public DeliverException() {
        super();
    }

    public DeliverException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public DeliverException(DeliverError deliverError) {
        super(deliverError.getMessage());
        this.errorCode = deliverError.getCode();
        this.errorMessage = deliverError.getMessage();
    }

    public DeliverException(DeliverError deliverError, String... parameters) {
        super(deliverError.getMessage());
        this.errorCode = deliverError.getCode();
        this.errorMessage = String.format(deliverError.getMessage(), parameters);
    }
}
