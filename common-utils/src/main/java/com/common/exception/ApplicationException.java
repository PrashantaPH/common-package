package com.common.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final String errorCode;

    public ApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, String errorCode, Throwable throwable) {
        super(message, throwable);
        this.errorCode = errorCode;
    }
}
