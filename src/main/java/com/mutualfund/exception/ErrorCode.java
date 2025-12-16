package com.mutualfund.exception;

import lombok.Getter;

/**
 * Enum representing all error codes used in the application. Each error code corresponds to a
 * specific error scenario and is mapped to user-friendly and developer-friendly messages.
 */
@Getter
public enum ErrorCode {
    // Resource not found errors
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    MUTUAL_FUND_NOT_FOUND("MUTUAL_FUND_NOT_FOUND"),
    HOLDING_NOT_FOUND("HOLDING_NOT_FOUND"),
    TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND"),

    // Business validation errors
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE"),
    INSUFFICIENT_UNITS("INSUFFICIENT_UNITS"),
    INVALID_AMOUNT("INVALID_AMOUNT"),
    INVALID_UNITS("INVALID_UNITS"),

    // Duplicate resource errors
    DUPLICATE_USER("DUPLICATE_USER"),
    DUPLICATE_FUND("DUPLICATE_FUND"),

    // Validation errors
    VALIDATION_ERROR("VALIDATION_ERROR"),

    // Security errors
    ACCESS_DENIED("ACCESS_DENIED"),
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED"),
    UNAUTHORIZED("UNAUTHORIZED"),

    // General errors
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    BAD_REQUEST("BAD_REQUEST"),

    // Operation specific errors
    NAV_UPDATE_FAILED("NAV_UPDATE_FAILED"),
    TRANSACTION_FAILED("TRANSACTION_FAILED");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    /**
     * Get error code from string value.
     *
     * @param code the error code string
     * @return the ErrorCode enum
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}
