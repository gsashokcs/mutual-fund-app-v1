package com.mutualfund.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.entity.Nav;
import com.mutualfund.model.request.AdminUserCreationRequest;
import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;
import com.mutualfund.model.response.UserResponse;
import com.mutualfund.service.MutualFundService;
import com.mutualfund.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

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

    /**
     * Creates a new mutual fund. NAV should be added separately using the Update NAV endpoint.
     *
     * @param request
     *            the mutual fund request with name
     * @return ResponseEntity with MutualFund and HTTP 201 status
     */
    @PostMapping("/funds")
    @Operation(summary = "Add mutual fund", description = "Creates a new mutual fund with metadata only. NAV values are managed separately for historical tracking. Use PUT /funds/{fundId}/nav to add NAV values for specific dates.")
    public ResponseEntity<MutualFund> addMutualFund(@Valid @RequestBody MutualFundRequest request) {
        MutualFund fund = mutualFundService.addMutualFund(request);
        return new ResponseEntity<>(fund, HttpStatus.CREATED);
    }

    /**
     * Updates or creates the NAV for a mutual fund on a specific date.
     *
     * @param fundId
     *            the ID of the fund to update
     * @param request
     *            the NAV update request with nav value and date
     * @return ResponseEntity with Nav entity and HTTP 200 status
     */
    @PutMapping("/funds/{fundId}/nav")
    @Operation(summary = "Update NAV", description = "Updates or creates Net Asset Value for a mutual fund on a specific date. If NAV exists for the date, it will be updated; otherwise, a new NAV entry will be created. This enables historical NAV tracking.")
    public ResponseEntity<Nav> updateNav(@PathVariable @Positive(message = "Fund ID must be positive") Long fundId, @Valid @RequestBody NavUpdateRequest request) {
        Nav nav = mutualFundService.updateNav(fundId, request);
        return ResponseEntity.ok(nav);
    }

    /**
     * Retrieves all mutual funds with pagination.
     *
     * @param page
     *            the page number (default 0)
     * @param size
     *            the page size (default 10)
     * @param sortBy
     *            the field to sort by (default fundId)
     * @return ResponseEntity with Page of MutualFund and HTTP 200 status
     */
    @GetMapping("/funds")
    @Operation(summary = "List all funds", description = "Retrieves all mutual funds in the system with pagination. Returns fund metadata only; NAV values should be queried separately using the NavRepository or transaction endpoints.")
    public ResponseEntity<Page<MutualFund>> getAllFunds(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "fundId") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<MutualFund> funds = mutualFundService.getAllMutualFunds(pageable);
        return ResponseEntity.ok(funds);
    }

    /**
     * Deletes a mutual fund from the system.
     *
     * @param fundId
     *            the ID of the fund to delete
     * @return ResponseEntity with HTTP 204 status
     */
    @DeleteMapping("/funds/{fundId}")
    @Operation(summary = "Delete fund", description = "Removes a mutual fund from the system. All associated NAV entries are soft-deleted for audit purposes before the fund is removed.")
    public ResponseEntity<Void> deleteFund(@PathVariable @Positive(message = "Fund ID must be positive") Long fundId) {
        mutualFundService.deleteMutualFund(fundId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates a new user with specified role (admin operation).
     *
     * @param request
     *            the user creation request with username, password, and role
     * @return ResponseEntity with UserResponse and HTTP 201 status
     */
    @PostMapping("/users")
    @Operation(summary = "Create user", description = "Creates a new user with specified role (USER or ADMIN)")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody AdminUserCreationRequest request) {
        UserResponse user = userService.createUser(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Retrieves all users with pagination.
     *
     * @param page
     *            the page number (default 0)
     * @param size
     *            the page size (default 10)
     * @param sortBy
     *            the field to sort by (default id)
     * @return ResponseEntity with Page of UserResponse and HTTP 200 status
     */
    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Retrieves all registered users with pagination")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Deletes a user from the system.
     *
     * @param userId
     *            the ID of the user to delete
     * @return ResponseEntity with HTTP 204 status
     */
    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete user", description = "Removes a user from the system")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
