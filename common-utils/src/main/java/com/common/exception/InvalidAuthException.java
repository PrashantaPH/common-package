package com.common.exception;

import lombok.Getter;

@Getter
public class InvalidAuthException extends RuntimeException {

    private final String errorCode;

    public InvalidAuthException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public InvalidAuthException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
