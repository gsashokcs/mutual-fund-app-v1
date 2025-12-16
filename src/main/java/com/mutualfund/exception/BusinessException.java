package com.mutualfund.exception;

import lombok.Getter;

/**
 * Exception thrown when a business rule or validation fails. This exception includes an error code
 * that maps to specific user and developer messages.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Constructor with error code.
     *
     * @param errorCode the error code
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getCode());
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code and custom message.
     *
     * @param errorCode the error code
     * @param message custom message
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error code and cause.
     *
     * @param errorCode the error code
     * @param cause the cause
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getCode(), cause);
        this.errorCode = errorCode;
    }
}
