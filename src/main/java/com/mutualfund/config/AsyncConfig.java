package com.mutualfund.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PreDestroy;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class for asynchronous execution using standard Java ExecutorService and ThreadPoolExecutor. Provides fixed thread pools for predictable resource usage and graceful shutdown capabilities.
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AsyncConfig.class);

    private ExecutorService customExecutorService;
    private ExecutorService transactionExecutorService;
    private ExecutorService notificationExecutorService;

    /**
     * Creates a custom thread factory with meaningful thread names. Helps in debugging and monitoring by providing identifiable thread names in logs and thread dumps.
     *
     * @param prefix the prefix for thread names
     * @return ThreadFactory with custom naming
     */
    private ThreadFactory createThreadFactory(String prefix) {
        return new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, prefix + threadNumber.getAndIncrement());
                thread.setDaemon(false);
                return thread;
            }
        };
    }

    /**
     * Creates a fixed thread pool executor for general async operations. Uses ThreadPoolExecutor with LinkedBlockingQueue for I/O-bound operations like API calls, database queries, and file processing.
     *
     * @return configured ThreadPoolExecutor wrapped as Executor
     */
    @Bean(name = "customExecutor")
    public Executor customExecutor() {
        customExecutorService = new ThreadPoolExecutor(5, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), createThreadFactory("custom-async-"), new ThreadPoolExecutor.CallerRunsPolicy());

        log.info("Custom async executor initialized - Fixed ThreadPoolExecutor with corePoolSize=5, maxPoolSize=10, queueCapacity=100");
        return customExecutorService;
    }

    /**
     * Creates a fixed thread pool executor for transaction processing. Uses ThreadPoolExecutor optimized for CPU-intensive transaction calculations and validations with higher concurrency.
     *
     * @return configured ThreadPoolExecutor for transaction processing
     */
    @Bean(name = "transactionExecutor")
    public Executor transactionExecutor() {
        transactionExecutorService = new ThreadPoolExecutor(10, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(200), createThreadFactory("transaction-async-"), new ThreadPoolExecutor.CallerRunsPolicy());

        log.info("Transaction async executor initialized - Fixed ThreadPoolExecutor with corePoolSize=10, maxPoolSize=20, queueCapacity=200");
        return transactionExecutorService;
    }

    /**
     * Creates a fixed thread pool executor for notification operations. Uses ThreadPoolExecutor for non-critical async tasks like sending emails, SMS, or push notifications with CallerRunsPolicy for backpressure handling.
     *
     * @return configured ThreadPoolExecutor for notifications
     */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        notificationExecutorService = new ThreadPoolExecutor(3, 8, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50), createThreadFactory("notification-async-"), new ThreadPoolExecutor.CallerRunsPolicy());

        log.info("Notification async executor initialized - Fixed ThreadPoolExecutor with corePoolSize=3, maxPoolSize=8, queueCapacity=50");
        return notificationExecutorService;
    }

    /**
     * Returns the default async executor used when no specific executor is specified in @Async annotation. Falls back to customExecutor for general async operations.
     *
     * @return the default executor
     */
    @Override
    public Executor getAsyncExecutor() {
        return customExecutor();
    }

    /**
     * Handles uncaught exceptions thrown by async methods. Logs the exception details including method name and exception message for debugging and monitoring purposes.
     *
     * @return AsyncUncaughtExceptionHandler implementation
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            log.error("Async method '{}' threw an uncaught exception: {}", method.getName(), throwable.getMessage(), throwable);
            log.error("Method parameters: {}", params);
        };
    }

    /**
     * Gracefully shuts down all executor services on application shutdown. Waits for running tasks to complete with configurable timeout periods ensuring no task loss during application restart.
     */
    @PreDestroy
    public void shutdownExecutors() {
        log.info("Shutting down async executors...");

        shutdownExecutor(customExecutorService, "customExecutor", 60);
        shutdownExecutor(transactionExecutorService, "transactionExecutor", 60);
        shutdownExecutor(notificationExecutorService, "notificationExecutor", 30);

        log.info("All async executors shut down successfully");
    }

    /**
     * Helper method to shutdown an executor service gracefully. Attempts graceful shutdown first, then forces shutdown if tasks don't complete within timeout.
     *
     * @param executorService the executor service to shutdown
     * @param name the name of the executor for logging
     * @param timeoutSeconds the timeout in seconds to wait for task completion
     */
    private void shutdownExecutor(ExecutorService executorService, String name, int timeoutSeconds) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                    log.warn("{} did not terminate gracefully, forcing shutdown", name);
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                        log.error("{} did not terminate after forced shutdown", name);
                    }
                } else {
                    log.info("{} shut down gracefully", name);
                }
            } catch (InterruptedException e) {
                log.error("{} shutdown interrupted, forcing shutdown", name);
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
