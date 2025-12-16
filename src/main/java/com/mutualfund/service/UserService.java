package com.mutualfund.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mutualfund.exception.BusinessException;
import com.mutualfund.exception.ErrorCode;
import com.mutualfund.exception.ResourceNotFoundException;
import com.mutualfund.model.entity.User;
import com.mutualfund.model.request.UserRegistrationRequest;
import com.mutualfund.model.response.UserResponse;
import com.mutualfund.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    /**
     * Registers a new user in the system.
     *
     * @param request the user registration request containing username and password
     * @return UserResponse containing the newly created user's details
     * @throws BusinessException if username already exists
     */
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_USER, "Username already exists: " + request.getUsername());
        }

        User user =
                User.builder()
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(User.Role.USER)
                        .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return mapToResponse(savedUser);
    }

    /**
     * Creates a new user with specified role (admin operation).
     *
     * @param request the user creation request containing username, password, and role
     * @return UserResponse containing the newly created user's details
     * @throws BusinessException if username already exists
     */
    @Transactional
    public UserResponse createUser(com.mutualfund.model.request.AdminUserCreationRequest request) {
        log.info(
                "Admin creating new user: {} with role: {}",
                request.getUsername(),
                request.getRole());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_USER, "Username already exists: " + request.getUsername());
        }

        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT, "Invalid role: " + request.getRole());
        }

        User user =
                User.builder()
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(role)
                        .build();

        User savedUser = userRepository.save(user);
        log.info(
                "User created successfully by admin with ID: {} and role: {}",
                savedUser.getId(),
                savedUser.getRole());

        return mapToResponse(savedUser);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return List of UserResponse containing all users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    /**
     * Retrieves all users with pagination support.
     *
     * @param pageable the pagination information
     * @return Page of UserResponse containing users
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info(
                "Fetching users with pagination: page {}, size {}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    /**
     * Retrieves a user by their ID. Regular users can only access their own profile. Admin users
     * can access any profile.
     *
     * @param userId the ID of the user to retrieve
     * @return UserResponse containing the user's details
     * @throws ResourceNotFoundException if user is not found
     * @throws BusinessException if user attempts to access another user's profile
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);

        // Validate user access - users can only view their own profile unless they are admin
        securityService.validateUserAccess(userId);

        User user =
                userRepository
                        .findById(
                                java.util.Objects.requireNonNull(userId, "User ID cannot be null"))
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ErrorCode.USER_NOT_FOUND,
                                                "User not found with ID: " + userId));
        return mapToResponse(user);
    }

    /**
     * Deletes a user from the system.
     *
     * @param userId the ID of the user to delete
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    ErrorCode.USER_NOT_FOUND, "User not found with ID: " + userId);
        }

        userRepository.deleteById(userId);
        log.info("User deleted successfully: {}", userId);
    }

    /**
     * Retrieves a user by their username. Regular users can only access their own profile. Admin
     * users can access any profile.
     *
     * @param username the username of the user to retrieve
     * @return UserResponse containing the user's details
     * @throws ResourceNotFoundException if user is not found
     * @throws BusinessException if user attempts to access another user's profile
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);

        // Validate user access - users can only view their own profile unless they are admin
        securityService.validateUserAccessByUsername(username);

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ErrorCode.USER_NOT_FOUND,
                                                "User not found: " + username));
        return mapToResponse(user);
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return User entity
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        ErrorCode.USER_NOT_FOUND, "User not found: " + username));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
