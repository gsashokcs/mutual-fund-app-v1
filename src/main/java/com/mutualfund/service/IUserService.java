package com.mutualfund.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mutualfund.model.entity.User;
import com.mutualfund.model.request.UserRegistrationRequest;
import com.mutualfund.model.response.UserResponse;

/**
 * Service interface for user management operations. Defines contract for user registration, retrieval, and deletion.
 */
public interface IUserService {

    /**
     * Registers a new user in the system.
     *
     * @param request
     *            the user registration request containing username and password
     * @return UserResponse containing the newly created user's details
     * @throws com.mutualfund.exception.BusinessException
     *             if username already exists
     */
    UserResponse registerUser(UserRegistrationRequest request);

    /**
     * Retrieves all users in the system.
     *
     * @return List of UserResponse containing all users
     */
    List<UserResponse> getAllUsers();

    /**
     * Retrieves all users with pagination support.
     *
     * @param pageable
     *            the pagination information
     * @return Page of UserResponse containing users
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId
     *            the ID of the user to retrieve
     * @return UserResponse containing the user's details
     * @throws com.mutualfund.exception.ResourceNotFoundException
     *             if user is not found
     */
    UserResponse getUserById(Long userId);

    /**
     * Deletes a user from the system.
     *
     * @param userId
     *            the ID of the user to delete
     * @throws com.mutualfund.exception.ResourceNotFoundException
     *             if user is not found
     */
    void deleteUser(Long userId);

    /**
     * Finds a user by their username.
     *
     * @param username
     *            the username to search for
     * @return User entity
     * @throws com.mutualfund.exception.ResourceNotFoundException
     *             if user is not found
     */
    User findByUsername(String username);
}
