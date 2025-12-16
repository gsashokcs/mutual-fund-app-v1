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
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info(
                "Fetching users with pagination: page {}, size {}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
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
