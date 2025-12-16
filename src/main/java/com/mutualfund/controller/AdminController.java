package com.mutualfund.controller;

import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;
import com.mutualfund.model.response.UserResponse;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.service.MutualFundService;
import com.mutualfund.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Admin Operations", description = "Administrative endpoints for managing funds and users")
public class AdminController {

    private final MutualFundService mutualFundService;
    private final UserService userService;

    @PostMapping("/funds")
    @Operation(summary = "Add mutual fund", description = "Creates a new mutual fund script with current date's NAV")
    public ResponseEntity<MutualFund> addMutualFund(@Valid @RequestBody MutualFundRequest request) {
        MutualFund fund = mutualFundService.addMutualFund(request);
        return new ResponseEntity<>(fund, HttpStatus.CREATED);
    }

    @PutMapping("/funds/{fundId}/nav")
    @Operation(summary = "Update NAV", description = "Updates the Net Asset Value for a mutual fund (current date only)")
    public ResponseEntity<MutualFund> updateNav(
            @PathVariable @Positive(message = "Fund ID must be positive") Long fundId,
            @Valid @RequestBody NavUpdateRequest request) {
        MutualFund fund = mutualFundService.updateNav(fundId, request);
        return ResponseEntity.ok(fund);
    }

    @GetMapping("/funds")
    @Operation(summary = "List all funds", description = "Retrieves all mutual funds in the system with pagination")
    public ResponseEntity<Page<MutualFund>> getAllFunds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fundId") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<MutualFund> funds = mutualFundService.getAllMutualFunds(pageable);
        return ResponseEntity.ok(funds);
    }

    @DeleteMapping("/funds/{fundId}")
    @Operation(summary = "Delete fund", description = "Removes a mutual fund from the system")
    public ResponseEntity<Void> deleteFund(
            @PathVariable @Positive(message = "Fund ID must be positive") Long fundId) {
        mutualFundService.deleteMutualFund(fundId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Retrieves all registered users with pagination")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user", description = "Removes a user from the system")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
