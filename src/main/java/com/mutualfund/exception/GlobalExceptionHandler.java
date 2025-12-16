package com.mutualfund.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ErrorMessageService errorMessageService;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        var errorCode = determineErrorCode(ex.getMessage());
        var errorDetail = errorMessageService.getErrorMessage(errorCode);
        
        var error = ErrorResponse.builder()
                .userMessage(errorDetail.getUserMessage())
                .devMessage(errorDetail.getDevMessage() + " - " + ex.getMessage())
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        var errorCode = determineErrorCode(ex.getMessage());
        var errorDetail = errorMessageService.getErrorMessage(errorCode);
        
        var error = ErrorResponse.builder()
                .userMessage(errorDetail.getUserMessage())
                .devMessage(errorDetail.getDevMessage() + " - " + ex.getMessage())
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        var message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        var errorDetail = errorMessageService.getErrorMessage("VALIDATION_ERROR");
        
        var error = ErrorResponse.builder()
                .userMessage(errorDetail.getUserMessage())
                .devMessage(errorDetail.getDevMessage() + " - " + message)
                .errorCode("VALIDATION_ERROR")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            jakarta.validation.ConstraintViolationException ex, WebRequest request) {
        
        var message = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.joining(", "));
        
        var errorDetail = errorMessageService.getErrorMessage("VALIDATION_ERROR");
        
        var error = ErrorResponse.builder()
                .userMessage(errorDetail.getUserMessage())
                .devMessage(errorDetail.getDevMessage() + " - " + message)
                .errorCode("VALIDATION_ERROR")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        var errorDetail = errorMessageService.getErrorMessage("ACCESS_DENIED");
        
        var error = ErrorResponse.builder()
                .userMessage(errorDetail.getUserMessage())
                .devMessage(errorDetail.getDevMessage())
                .errorCode("ACCESS_DENIED")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        var errorDetail = errorMessageService.getErrorMessage("INTERNAL_SERVER_ERROR");
        
        var error = ErrorResponse.builder()
                .userMessage(errorDetail.getUserMessage())
                .devMessage(errorDetail.getDevMessage() + " - " + ex.getMessage())
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String determineErrorCode(String message) {
        if (message == null) return "RESOURCE_NOT_FOUND";
        
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("insufficient balance")) {
            return "INSUFFICIENT_BALANCE";
        } else if (lowerMessage.contains("insufficient units")) {
            return "INSUFFICIENT_UNITS";
        } else if (lowerMessage.contains("duplicate") && lowerMessage.contains("user")) {
            return "DUPLICATE_USER";
        } else if (lowerMessage.contains("duplicate") && lowerMessage.contains("fund")) {
            return "DUPLICATE_FUND";
        } else if (lowerMessage.contains("invalid amount")) {
            return "INVALID_AMOUNT";
        } else if (lowerMessage.contains("invalid units")) {
            return "INVALID_UNITS";
        } else if (lowerMessage.contains("mutual fund") || lowerMessage.contains("fund")) {
            return "MUTUAL_FUND_NOT_FOUND";
        } else if (lowerMessage.contains("holding")) {
            return "HOLDING_NOT_FOUND";
        } else if (lowerMessage.contains("transaction")) {
            return "TRANSACTION_NOT_FOUND";
        } else if (lowerMessage.contains("user")) {
            return "USER_NOT_FOUND";
        } else {
            return "RESOURCE_NOT_FOUND";
        }
    }
}
