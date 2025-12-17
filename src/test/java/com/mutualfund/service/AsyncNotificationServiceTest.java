package com.mutualfund.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for AsyncNotificationService. Tests async notification methods including email, SMS, audit logging, and multi-channel notifications to verify async execution and error handling.
 */
@ExtendWith(MockitoExtension.class)
class AsyncNotificationServiceTest {

    @InjectMocks
    private AsyncNotificationService asyncNotificationService;

    @BeforeEach
    void setUp() {
        // No setup required for this test
    }

    @Test
    void sendEmailNotificationSuccess() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = asyncNotificationService.sendEmailNotification("test@example.com", "Test Subject", "Test Body");

        // Verify that future completes successfully
        assertNotNull(future);
        assertDoesNotThrow(() -> future.get());
    }

    @Test
    void sendSmsNotificationSuccess() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> future = asyncNotificationService.sendSmsNotification("+1234567890", "Test SMS message");

        // Verify that future completes successfully
        assertNotNull(future);
        assertTrue(future.get());
    }

    @Test
    void logAuditEventSuccess() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = asyncNotificationService.logAuditEvent(1L, "TEST_ACTION", "Test audit details");

        // Verify that future completes successfully
        assertNotNull(future);
        assertDoesNotThrow(() -> future.get());
    }

    @Test
    void sendMultiChannelNotificationSuccess() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = asyncNotificationService.sendMultiChannelNotification("test@example.com", "Test Subject", "Test Body", "+1234567890", "Test SMS");

        // Verify that future completes successfully
        assertNotNull(future);
        String result = future.get();
        assertEquals("All notifications sent successfully", result);
    }
}
