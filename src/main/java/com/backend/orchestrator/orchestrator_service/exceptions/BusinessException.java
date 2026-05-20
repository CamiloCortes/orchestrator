package com.backend.orchestrator.orchestrator_service.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    public BusinessException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public BusinessException(String message) {
        this("BUSINESS_ERROR", message, 400);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
        this.httpStatus = 400;
    }

    public BusinessException(String errorCode, String message) {
        this(errorCode, message, 400);
    }
}
