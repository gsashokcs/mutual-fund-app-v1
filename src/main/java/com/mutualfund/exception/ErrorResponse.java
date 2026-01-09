package com.mutualfund.exception;

import java.time.LocalDateTime;
import java.util.Objects;

public class ErrorResponse {
    private String userMessage;
    private String devMessage;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(String userMessage, String devMessage, String errorCode, LocalDateTime timestamp, String path) {
        this.userMessage = userMessage;
        this.devMessage = devMessage;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
        this.path = path;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getDevMessage() {
        return devMessage;
    }

    public void setDevMessage(String devMessage) {
        this.devMessage = devMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return Objects.equals(userMessage, that.userMessage) && Objects.equals(devMessage, that.devMessage) && Objects.equals(errorCode, that.errorCode) && Objects.equals(timestamp, that.timestamp) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userMessage, devMessage, errorCode, timestamp, path);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" + "userMessage='" + userMessage + '\'' + ", devMessage='" + devMessage + '\'' + ", errorCode='" + errorCode + '\'' + ", timestamp=" + timestamp + ", path='" + path + '\'' + '}';
    }

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {
        private String userMessage;
        private String devMessage;
        private String errorCode;
        private LocalDateTime timestamp;
        private String path;

        ErrorResponseBuilder() {
        }

        public ErrorResponseBuilder userMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        public ErrorResponseBuilder devMessage(String devMessage) {
            this.devMessage = devMessage;
            return this;
        }

        public ErrorResponseBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(userMessage, devMessage, errorCode, timestamp, path);
        }
    }

    public static void main(String[] args) {
        
    }
}
