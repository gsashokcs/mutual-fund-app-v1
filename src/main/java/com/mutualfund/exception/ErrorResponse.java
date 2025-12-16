package com.mutualfund.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private String userMessage;
    private String devMessage;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;
}
