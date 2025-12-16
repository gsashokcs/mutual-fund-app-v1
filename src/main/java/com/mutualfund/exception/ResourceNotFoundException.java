package com.mutualfund.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found. This exception includes an error code
 * that maps to specific user and developer messages.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Constructor with error code.
     *
     * @param errorCode the error code
     */
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code and custom message.
     *
     * @param errorCode the error code
     * @param message custom message
     */
    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code and cause.
     *
     * @param errorCode the error code
     * @param cause the cause
     */
    public ResourceNotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getCode(), cause);
        this.errorCode = errorCode;
    }
}
