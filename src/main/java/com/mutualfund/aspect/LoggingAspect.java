package com.mutualfund.aspect;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AOP Aspect for logging method execution details including request parameters, response values, and execution time. This aspect intercepts all controller methods to provide comprehensive request-response logging.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggingAspect.class);

    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Pointcut for all controller methods in the application. Matches any method in classes within the com.mutualfund.controller package.
     */
    @Pointcut("within(com.mutualfund.controller..*)")
    public void controllerMethods() {}

    /**
     * Around advice that logs request parameters, execution time, and response for all controller methods. Logs method entry with parameters, execution time, successful responses, and exceptions if thrown.
     *
     * @param joinPoint the proceeding join point providing access to method details
     * @return the result of the method execution
     * @throws Throwable if the underlying method throws an exception
     */
    @Around("controllerMethods()")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        Instant start = Instant.now();

        try {
            String requestParams = formatArguments(args);
            log.info(">>> REQUEST: {}.{} - Parameters: {}", className, methodName, requestParams);

            Object result = joinPoint.proceed();

            long executionTime = Duration.between(start, Instant.now()).toMillis();
            String responseBody = formatResponse(result);
            log.info("<<< RESPONSE: {}.{} - Execution time: {}ms - Response: {}", className, methodName, executionTime, responseBody);

            return result;
        } catch (Exception ex) {
            long executionTime = Duration.between(start, Instant.now()).toMillis();
            log.error("!!! EXCEPTION: {}.{} - Execution time: {}ms - Exception: {}", className, methodName, executionTime, ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * Formats method arguments as a JSON string. Filters out sensitive types like HttpServletRequest and HttpServletResponse.
     *
     * @param args the method arguments
     * @return formatted JSON string representation of arguments
     */
    private String formatArguments(Object[] args) {
        try {
            if (args == null || args.length == 0) {
                return "[]";
            }

            Object[] filteredArgs = Arrays.stream(args).filter(arg -> arg != null && !arg.getClass().getName().startsWith("org.springframework") && !arg.getClass().getName().startsWith("jakarta.servlet")).toArray();

            return objectMapper.writeValueAsString(filteredArgs);
        } catch (Exception e) {
            log.debug("Failed to serialize arguments: {}", e.getMessage());
            return Arrays.toString(args);
        }
    }

    /**
     * Formats the response object as a JSON string. Handles ResponseEntity by extracting the body. Limits large responses to prevent log overflow.
     *
     * @param result the method return value
     * @return formatted JSON string representation of response
     */
    private String formatResponse(Object result) {
        try {
            if (result == null) {
                return "null";
            }

            if (result instanceof org.springframework.http.ResponseEntity<?>) {
                org.springframework.http.ResponseEntity<?> responseEntity = (org.springframework.http.ResponseEntity<?>) result;
                Object body = responseEntity.getBody();
                String bodyJson = objectMapper.writeValueAsString(body);

                if (bodyJson.length() > 1000) {
                    return String.format("{\"status\":%d, \"body\":\"%s...\" (truncated, %d chars)}", responseEntity.getStatusCode().value(), bodyJson.substring(0, 1000), bodyJson.length());
                }

                return String.format("{\"status\":%d, \"body\":%s}", responseEntity.getStatusCode().value(), bodyJson);
            }

            String json = objectMapper.writeValueAsString(result);
            return json.length() > 1000 ? json.substring(0, 1000) + "... (truncated)" : json;
        } catch (Exception e) {
            log.debug("Failed to serialize response: {}", e.getMessage());
            return result.toString();
        }
    }
}
