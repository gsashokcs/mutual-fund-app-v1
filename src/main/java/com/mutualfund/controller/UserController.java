package com.mutualfund.controller;

import jakarta.validation.Valid;

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

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "User Management", description = "User registration and profile management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user account.
     *
     * @param request
     *            the user registration request with username and password
     * @return ResponseEntity with UserResponse and HTTP 201 status
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with USER role")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a user's profile by their username.
     *
     * @param username
     *            the username of the user to retrieve
     * @return ResponseEntity with UserResponse and HTTP 200 status
     */
    @GetMapping("/{username}")
    @SecurityRequirement(name = "basicAuth")
    @Operation(summary = "Get user profile", description = "Retrieves user profile information by username")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }
}
