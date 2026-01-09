package com.mutualfund.config;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String USERNAME = "username";

    /**
     * Adds transaction ID and username to MDC for logging context.
     *
     * @param request
     *            the HTTP request
     * @param response
     *            the HTTP response
     * @param filterChain
     *            the filter chain
     * @throws ServletException
     *             if servlet error occurs
     * @throws IOException
     *             if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                MDC.put(USERNAME, authentication.getName());
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
