package com.mutualfund.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mutualfund.exception.BusinessException;
import com.mutualfund.exception.ErrorCode;
import com.mutualfund.model.entity.User;
import com.mutualfund.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling security and authorization checks. Validates user ownership and role-based access control.
 *
 * @author Mutual Fund Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    /**
     * Validates that the authenticated user can access resources for the given userId. Admin users can access any user's resources. Regular users can only access their own resources.
     *
     * @param userId
     *            the ID of the user whose resources are being accessed
     * @throws BusinessException
     *             if access is denied
     */
    public void validateUserAccess(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Check if user is admin
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return; // Admin can access any user's data
        }

        // Get the authenticated user's ID
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Check if the authenticated user is accessing their own data
        if (!authenticatedUser.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    /**
     * Validates that the authenticated user can access resources for the given username. Admin users can access any user's resources. Regular users can only access their own resources.
     *
     * @param username
     *            the username of the user whose resources are being accessed
     * @throws BusinessException
     *             if access is denied
     */
    public void validateUserAccessByUsername(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        // Check if user is admin
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return; // Admin can access any user's data
        }

        // Check if the authenticated user is accessing their own data
        if (!authenticatedUsername.equals(username)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    /**
     * Gets the currently authenticated user's username.
     *
     * @return the username of the authenticated user
     */
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * Gets the currently authenticated user.
     *
     * @return the authenticated User entity
     * @throws BusinessException
     *             if user is not found
     */
    public User getAuthenticatedUser() {
        String username = getAuthenticatedUsername();
        return userRepository.findByUsername(username).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Checks if the currently authenticated user is an admin.
     *
     * @return true if the user has ADMIN role, false otherwise
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
