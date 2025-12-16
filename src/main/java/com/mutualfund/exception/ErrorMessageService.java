package com.mutualfund.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ErrorMessageService {

    private Map<String, ErrorMessageDetail> errorMessages = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void loadErrorMessages() {
        try {
            ClassPathResource resource = new ClassPathResource("error-messages.json");
            errorMessages = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<Map<String, ErrorMessageDetail>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load error messages from JSON file", e);
        }
    }

    public ErrorMessageDetail getErrorMessage(String errorCode) {
        return errorMessages.getOrDefault(errorCode, 
                new ErrorMessageDetail(
                        "An error occurred.",
                        "Error code: " + errorCode
                ));
    }

    public static class ErrorMessageDetail {
        private String userMessage;
        private String devMessage;

        public ErrorMessageDetail() {}

        public ErrorMessageDetail(String userMessage, String devMessage) {
            this.userMessage = userMessage;
            this.devMessage = devMessage;
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
    }
}
