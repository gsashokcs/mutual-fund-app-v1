package com.mutualfund.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorMessageService errorMessageService;

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {} - ErrorCode: {}", ex.getMessage(), ex.getErrorCode());

        ErrorMessageService.ErrorMessageDetail errorDetail = errorMessageService.getErrorMessage(ex.getErrorCode().getCode());

        String devMessage = errorDetail.getDevMessage();
        if (ex.getMessage() != null && !ex.getMessage().equals(ex.getErrorCode().getCode())) {
            devMessage += " - " + ex.getMessage();
        }

        ErrorResponse error = buildErrorResponse(errorDetail.getUserMessage(), devMessage, ex.getErrorCode().getCode(), request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        log.warn("Business exception: {} - ErrorCode: {}", ex.getMessage(), ex.getErrorCode());

        ErrorMessageService.ErrorMessageDetail errorDetail = errorMessageService.getErrorMessage(ex.getErrorCode().getCode());

        String devMessage = errorDetail.getDevMessage();
        if (ex.getMessage() != null && !ex.getMessage().equals(ex.getErrorCode().getCode())) {
            devMessage += " - " + ex.getMessage();
        }

        ErrorResponse error = buildErrorResponse(errorDetail.getUserMessage(), devMessage, ex.getErrorCode().getCode(), request);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation error: {} errors found", ex.getBindingResult().getErrorCount());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        String message = validationErrors.values().stream().collect(Collectors.joining(", "));

        ErrorMessageService.ErrorMessageDetail errorDetail = errorMessageService.getErrorMessage(ErrorCode.VALIDATION_ERROR.getCode());

        ErrorResponse error = buildErrorResponse(errorDetail.getUserMessage(), errorDetail.getDevMessage() + " - " + message, ErrorCode.VALIDATION_ERROR.getCode(), request);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.warn("Constraint violation: {} violations found", ex.getConstraintViolations().size());

        String message = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));

        ErrorMessageService.ErrorMessageDetail errorDetail = errorMessageService.getErrorMessage(ErrorCode.VALIDATION_ERROR.getCode());

        ErrorResponse error = buildErrorResponse(errorDetail.getUserMessage(), errorDetail.getDevMessage() + " - " + message, ErrorCode.VALIDATION_ERROR.getCode(), request);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());

        ErrorMessageService.ErrorMessageDetail errorDetail = errorMessageService.getErrorMessage(ErrorCode.ACCESS_DENIED.getCode());

        ErrorResponse error = buildErrorResponse(errorDetail.getUserMessage(), errorDetail.getDevMessage(), ErrorCode.ACCESS_DENIED.getCode(), request);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        log.warn("Authentication failed: Invalid credentials");

        ErrorMessageService.ErrorMessageDetail errorDetail = errorMessageService.getErrorMessage(ErrorCode.AUTHENTICATION_FAILED.getCode());

        ErrorResponse error = buildErrorResponse(errorDetail.getUserMessage(), errorDetail.getDevMessage() + ": " + ex.getMessage(), ErrorCode.AUTHENTICATION_FAILED.getCode(), request);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.warn("Type mismatch: {} cannot be converted to {}", ex.getValue(), ex.getRequiredType());

        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s", ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());

        ErrorResponse error = buildErrorResponse("Invalid request parameter type", message, "TYPE_MISMATCH", request);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        log.warn("Method not supported: {} for path {}", ex.getMethod(), extractPath(request));

        String message = String.format("HTTP method '%s' is not supported for this endpoint. " + "Supported methods: %s", ex.getMethod(), ex.getSupportedHttpMethods());

        ErrorResponse error = buildErrorResponse("HTTP method not supported", message, "METHOD_NOT_ALLOWED", request);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        log.warn("Media type not supported: {}", ex.getContentType());

        String message = String.format("Content type '%s' is not supported. " + "Supported types: %s", ex.getContentType(), ex.getSupportedMediaTypes());

        ErrorResponse error = buildErrorResponse("Unsupported media type", message, "UNSUPPORTED_MEDIA_TYPE", request);

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.warn("Message not readable: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse("Malformed JSON request", "Failed to read request body: " + ex.getMostSpecificCause().getMessage(), "MALFORMED_JSON", request);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex, WebRequest request) {
        log.warn("Missing required parameter: {} of type {}", ex.getParameterName(), ex.getParameterType());

        String message = String.format("Required parameter '%s' of type '%s' is missing", ex.getParameterName(), ex.getParameterType());

        ErrorResponse error = buildErrorResponse("Missing required parameter", message, "MISSING_PARAMETER", request);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, WebRequest request) {
        log.warn("No handler found: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        String message = String.format("No endpoint found for %s %s", ex.getHttpMethod(), ex.getRequestURL());

        ErrorResponse error = buildErrorResponse("Endpoint not found", message, "ENDPOINT_NOT_FOUND", request);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse("Data integrity constraint violation", "Database constraint violated: " + ex.getMostSpecificCause().getMessage(), "DATA_INTEGRITY_VIOLATION", request);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse("Invalid argument provided", ex.getMessage(), "ILLEGAL_ARGUMENT", request);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        ErrorMessageService.ErrorMessageDetail errorDetail = errorMessageService.getErrorMessage(ErrorCode.INTERNAL_SERVER_ERROR.getCode());

        ErrorResponse error = buildErrorResponse(errorDetail.getUserMessage(), errorDetail.getDevMessage() + " - " + ex.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR.getCode(), request);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private ErrorResponse buildErrorResponse(String userMessage, String devMessage, String errorCode, WebRequest request) {
        return ErrorResponse.builder().userMessage(userMessage).devMessage(devMessage).errorCode(errorCode).timestamp(LocalDateTime.now()).path(extractPath(request)).build();
    }

    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }
}
