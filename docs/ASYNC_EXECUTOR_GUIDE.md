# Async Executor Service Configuration

## Overview

The application includes a comprehensive async executor service configuration that enables asynchronous method execution for improved performance and non-blocking operations. This is particularly useful for I/O-bound operations, notifications, and background processing tasks.

## Configuration Components

### 1. AsyncConfig.java

Located in `com.mutualfund.config.AsyncConfig`, this configuration class provides:

- **@EnableAsync**: Enables Spring's async method execution capability
- **AsyncConfigurer**: Implements interface for custom async configuration
- **Multiple Thread Pool Executors**: Defines specialized executors for different use cases

### 2. Thread Pool Executors

#### Custom Executor (General Purpose)
```java
@Bean(name = "customExecutor")
- Core Pool Size: 5 threads
- Max Pool Size: 10 threads
- Queue Capacity: 100 tasks
- Thread Name Prefix: "custom-async-"
- Use Case: General async operations, API calls, database queries
```

#### Transaction Executor (High Concurrency)
```java
@Bean(name = "transactionExecutor")
- Core Pool Size: 10 threads
- Max Pool Size: 20 threads
- Queue Capacity: 200 tasks
- Thread Name Prefix: "transaction-async-"
- Use Case: CPU-intensive transaction calculations and validations
```

#### Notification Executor (Low Priority)
```java
@Bean(name = "notificationExecutor")
- Core Pool Size: 3 threads
- Max Pool Size: 8 threads
- Queue Capacity: 50 tasks
- Thread Name Prefix: "notification-async-"
- Use Case: Email, SMS, push notifications
```

### 3. Exception Handling

The configuration includes a custom `AsyncUncaughtExceptionHandler` that:
- Logs all uncaught exceptions from async methods
- Records method name and parameters for debugging
- Prevents silent failures in background tasks

## Usage Examples

### Basic Async Method

```java
@Service
public class MyService {
    
    @Async("customExecutor")
    public CompletableFuture<String> processAsync() {
        // Async processing logic
        return CompletableFuture.completedFuture("Done");
    }
}
```

### Multiple Async Operations

```java
@Service
public class NotificationService {
    
    @Async("notificationExecutor")
    public CompletableFuture<Void> sendEmail(String recipient) {
        // Email sending logic
        return CompletableFuture.completedFuture(null);
    }
    
    @Async("notificationExecutor")
    public CompletableFuture<Boolean> sendSms(String phone) {
        // SMS sending logic
        return CompletableFuture.completedFuture(true);
    }
}
```

### Combining Multiple Async Tasks

```java
CompletableFuture<Void> email = sendEmail("user@example.com");
CompletableFuture<Boolean> sms = sendSms("+1234567890");

CompletableFuture.allOf(email, sms)
    .thenRun(() -> log.info("All notifications sent"))
    .exceptionally(ex -> {
        log.error("Notification failed: {}", ex.getMessage());
        return null;
    });
```

## Implementation in Services

### AsyncNotificationService

A dedicated service demonstrating async patterns:

1. **Email Notifications**: Async email sending without blocking
2. **SMS Notifications**: Background SMS delivery
3. **Audit Logging**: Non-blocking audit event recording
4. **Multi-Channel**: Parallel execution of multiple notification channels

### UserService Integration

The `UserService` has been enhanced to use async notifications:

```java
@Transactional
public UserResponse registerUser(UserRegistrationRequest request) {
    // Synchronous user registration
    User savedUser = userRepository.save(user);
    
    // Async welcome email (non-blocking)
    asyncNotificationService.sendEmailNotification(
        savedUser.getUsername(), 
        "Welcome", 
        "Account created successfully"
    );
    
    // Async audit logging (non-blocking)
    asyncNotificationService.logAuditEvent(
        savedUser.getId(), 
        "USER_REGISTRATION", 
        "New user: " + savedUser.getUsername()
    );
    
    return mapToResponse(savedUser);
}
```

## Benefits

1. **Improved Response Times**: Main thread doesn't wait for notifications
2. **Better Resource Utilization**: Separate thread pools for different operations
3. **Graceful Degradation**: Non-critical tasks don't block critical operations
4. **Scalability**: Independent scaling of different async operations
5. **Exception Handling**: Centralized error handling for async methods

## Monitoring

### Thread Pool Metrics

Monitor executor health via Spring Boot Actuator:
- `/actuator/metrics/executor.active` - Active thread count
- `/actuator/metrics/executor.pool.size` - Current pool size
- `/actuator/metrics/executor.queue.remaining` - Queue capacity

### Logging

All executors log initialization details:
```
Custom async executor initialized with corePoolSize=5, maxPoolSize=10, queueCapacity=100
Transaction async executor initialized with corePoolSize=10, maxPoolSize=20, queueCapacity=200
Notification async executor initialized with corePoolSize=3, maxPoolSize=8, queueCapacity=50
```

## Best Practices

1. **Choose Appropriate Executor**: Use the right executor for your use case
2. **Return CompletableFuture**: Always return CompletableFuture from @Async methods
3. **Handle Exceptions**: Implement proper exception handling in async methods
4. **Avoid Blocking Calls**: Don't use blocking operations in async methods
5. **Test Async Methods**: Use CompletableFuture.get() or awaitility for testing

## Configuration Tuning

Adjust thread pool sizes based on your needs:

```java
// For I/O-bound tasks (API calls, DB queries)
corePoolSize = 2 * CPU_CORES
maxPoolSize = 4 * CPU_CORES

// For CPU-bound tasks (calculations)
corePoolSize = CPU_CORES
maxPoolSize = 2 * CPU_CORES
```

## Graceful Shutdown

All executors are configured with:
```java
executor.setWaitForTasksToCompleteOnShutdown(true);
executor.setAwaitTerminationSeconds(60);
```

This ensures:
- Running tasks complete before shutdown
- Maximum 60 seconds wait time
- No task loss during application restart

## Testing

See `AsyncNotificationServiceTest` for examples of testing async methods:
- Use `CompletableFuture.get()` to wait for completion
- Verify return values and exceptions
- Test timeout scenarios with `get(timeout, TimeUnit)`

## Troubleshooting

### Common Issues

1. **Methods not executing async**: Ensure @EnableAsync is present
2. **Self-invocation doesn't work**: Call async methods from different beans
3. **Exceptions swallowed**: Check AsyncUncaughtExceptionHandler logs
4. **Thread pool exhaustion**: Monitor queue size and adjust pool configuration

### Debug Logging

Enable debug logging for async execution:
```properties
logging.level.org.springframework.scheduling=DEBUG
logging.level.com.mutualfund.config.AsyncConfig=DEBUG
```

## Dependencies

Required dependency (already included in pom.xml):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

## Future Enhancements

Potential improvements:
1. Add metrics collection for async operations
2. Implement retry logic for failed async tasks
3. Add circuit breaker for external service calls
4. Create dedicated executor for scheduled tasks
5. Implement async result caching

---

**Last Updated**: December 2025  
**Version**: 1.0.0
