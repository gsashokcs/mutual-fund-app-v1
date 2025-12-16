package com.mutualfund.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mutualfund.model.request.UserRegistrationRequest;
import com.mutualfund.model.response.UserResponse;
import com.mutualfund.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "User registration and profile management endpoints")
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user account.
     *
     * @param request the user registration request with username and password
     * @return ResponseEntity with UserResponse and HTTP 201 status
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with USER role")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a user's profile by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return ResponseEntity with UserResponse and HTTP 200 status
     */
    @GetMapping("/{userId}")
    @SecurityRequirement(name = "basicAuth")
    @Operation(
            summary = "Get user profile",
            description = "Retrieves user profile information by user ID")
    public ResponseEntity<UserResponse> getUserProfile(
            @PathVariable @Positive(message = "User ID must be positive") Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
}
