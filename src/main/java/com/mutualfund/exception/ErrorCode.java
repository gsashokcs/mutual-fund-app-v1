package com.mutualfund.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    MUTUAL_FUND_NOT_FOUND("MUTUAL_FUND_NOT_FOUND"),
    HOLDING_NOT_FOUND("HOLDING_NOT_FOUND"),
    TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND"),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE"),
    INSUFFICIENT_UNITS("INSUFFICIENT_UNITS"),
    INVALID_AMOUNT("INVALID_AMOUNT"),
    INVALID_UNITS("INVALID_UNITS"),
    DUPLICATE_USER("DUPLICATE_USER"),
    DUPLICATE_FUND("DUPLICATE_FUND"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    ACCESS_DENIED("ACCESS_DENIED"),
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED"),
    UNAUTHORIZED("UNAUTHORIZED"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    BAD_REQUEST("BAD_REQUEST"),
    NAV_UPDATE_FAILED("NAV_UPDATE_FAILED"),
    TRANSACTION_FAILED("TRANSACTION_FAILED");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}
