package com.mutualfund.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ErrorMessageService {

    private Map<String, ErrorMessageDetail> errorMessages = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Loads error messages from error-messages.json file on application startup. */
    @PostConstruct
    public void loadErrorMessages() {
        try {
            ClassPathResource resource = new ClassPathResource("error-messages.json");
            errorMessages =
                    objectMapper.readValue(
                            resource.getInputStream(),
                            new TypeReference<Map<String, ErrorMessageDetail>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load error messages from JSON file", e);
        }
    }

    /**
     * Retrieves error message details for a given error code.
     *
     * @param errorCode the error code to look up
     * @return ErrorMessageDetail containing user and developer messages
     */
    public ErrorMessageDetail getErrorMessage(String errorCode) {
        return errorMessages.getOrDefault(
                errorCode,
                new ErrorMessageDetail("An error occurred.", "Error code: " + errorCode));
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
