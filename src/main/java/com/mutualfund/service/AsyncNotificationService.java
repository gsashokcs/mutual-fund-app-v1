package com.mutualfund.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for handling asynchronous notification operations. Demonstrates usage of async executors for non-blocking notification delivery including email, SMS, and audit logging.
 */
@Service
public class AsyncNotificationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AsyncNotificationService.class);

    /**
     * Sends an email notification asynchronously using the notification executor. This method runs in a separate thread and doesn't block the main application flow, making it suitable for non-critical notifications.
     *
     * @param recipient the email recipient
     * @param subject the email subject
     * @param body the email body content
     * @return CompletableFuture that completes when email is sent
     */
    @Async("notificationExecutor")
    public CompletableFuture<Void> sendEmailNotification(String recipient, String subject, String body) {
        log.info("Sending email notification to: {} - Subject: {}", recipient, subject);
        try {
            // Simulate email sending delay
            Thread.sleep(2000);
            log.info("Email sent successfully to: {}", recipient);
        } catch (InterruptedException e) {
            log.error("Email sending interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Sends an SMS notification asynchronously using the notification executor. Processes SMS sending in a background thread without blocking the caller, ideal for OTP and alert messages.
     *
     * @param phoneNumber the recipient phone number
     * @param message the SMS message content
     * @return CompletableFuture that completes when SMS is sent
     */
    @Async("notificationExecutor")
    public CompletableFuture<Boolean> sendSmsNotification(String phoneNumber, String message) {
        log.info("Sending SMS notification to: {} - Message: {}", phoneNumber, message);
        try {
            // Simulate SMS sending delay
            Thread.sleep(1500);
            log.info("SMS sent successfully to: {}", phoneNumber);
            return CompletableFuture.completedFuture(true);
        } catch (InterruptedException e) {
            log.error("SMS sending interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Logs audit information asynchronously using the custom executor. Records audit events in background threads to avoid performance impact on main business logic, useful for compliance and security monitoring.
     *
     * @param userId the user ID performing the action
     * @param action the action performed
     * @param details additional audit details
     * @return CompletableFuture that completes when audit is logged
     */
    @Async("customExecutor")
    public CompletableFuture<Void> logAuditEvent(Long userId, String action, String details) {
        log.info("Logging audit event - User: {}, Action: {}, Details: {}", userId, action, details);
        try {
            // Simulate audit log writing
            Thread.sleep(500);
            log.info("Audit event logged successfully for user: {}", userId);
        } catch (InterruptedException e) {
            log.error("Audit logging interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Sends multiple notifications concurrently using async executor. Demonstrates parallel execution of multiple async tasks, combining email and SMS notifications for comprehensive user communication.
     *
     * @param recipient the notification recipient
     * @param emailSubject the email subject
     * @param emailBody the email body
     * @param phoneNumber the phone number for SMS
     * @param smsMessage the SMS message
     * @return CompletableFuture that completes when all notifications are sent
     */
    @Async("notificationExecutor")
    public CompletableFuture<String> sendMultiChannelNotification(String recipient, String emailSubject, String emailBody, String phoneNumber, String smsMessage) {
        log.info("Sending multi-channel notification to: {}", recipient);

        CompletableFuture<Void> emailFuture = sendEmailNotification(recipient, emailSubject, emailBody);
        CompletableFuture<Boolean> smsFuture = sendSmsNotification(phoneNumber, smsMessage);

        return CompletableFuture.allOf(emailFuture, smsFuture).thenApply(v -> {
            log.info("Multi-channel notification completed for: {}", recipient);
            return "All notifications sent successfully";
        }).exceptionally(ex -> {
            log.error("Multi-channel notification failed: {}", ex.getMessage());
            return "Notification sending failed";
        });
    }
}
