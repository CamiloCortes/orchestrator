package com.backend.orchestrator.orchestrator_service.exceptions;

public class SessionExpiredException extends BusinessException {

    public SessionExpiredException(String message) {
        super(message);
    }

    public SessionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
