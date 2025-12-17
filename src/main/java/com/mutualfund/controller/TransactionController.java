package com.mutualfund.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.mutualfund.model.request.TransactionRequest;
import com.mutualfund.model.response.HoldingResponse;
import com.mutualfund.model.response.TransactionResponse;
import com.mutualfund.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users/{userId}")
@Validated
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Transactions", description = "User transaction and holdings management endpoints")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Processes a buy transaction for mutual fund units.
     *
     * @param userId
     *            the ID of the user making the purchase
     * @param request
     *            the transaction request with fund ID and units
     * @return ResponseEntity with TransactionResponse and HTTP 201 status
     */
    @PostMapping("/buy")
    @Operation(summary = "Buy units", description = "Purchases mutual fund units at the latest available NAV. The system fetches the most recent NAV from the historical NAV table for the specified fund.")
    public ResponseEntity<TransactionResponse> buyUnits(@PathVariable @Positive(message = "User ID must be positive") Long userId, @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.buyUnits(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Processes a redemption transaction for mutual fund units.
     *
     * @param userId
     *            the ID of the user redeeming units
     * @param request
     *            the transaction request with fund ID and units
     * @return ResponseEntity with TransactionResponse and HTTP 201 status
     */
    @PostMapping("/redeem")
    @Operation(summary = "Redeem units", description = "Redeems mutual fund units at the latest available NAV. The system fetches the most recent NAV from the historical NAV table for the specified fund. Validates sufficient units before processing.")
    public ResponseEntity<TransactionResponse> redeemUnits(@PathVariable @Positive(message = "User ID must be positive") Long userId, @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.redeemUnits(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all mutual fund holdings for a user.
     *
     * @param userId
     *            the ID of the user
     * @return ResponseEntity with List of HoldingResponse and HTTP 200 status
     */
    @GetMapping("/holdings")
    @Operation(summary = "View holdings", description = "Retrieves all mutual fund holdings for a user with current market values calculated using the latest NAV from the historical NAV table.")
    public ResponseEntity<List<HoldingResponse>> getHoldings(@PathVariable @Positive(message = "User ID must be positive") Long userId) {
        List<HoldingResponse> holdings = transactionService.getUserHoldings(userId);
        return ResponseEntity.ok(holdings);
    }

    /**
     * Retrieves transaction history for a user with pagination.
     *
     * @param userId
     *            the ID of the user
     * @param page
     *            the page number (default 0)
     * @param size
     *            the page size (default 10)
     * @param sortBy
     *            the field to sort by (default transactionDate)
     * @return ResponseEntity with Page of TransactionResponse and HTTP 200 status
     */
    @GetMapping("/transactions")
    @Operation(summary = "View transactions", description = "Retrieves transaction history for a user with pagination")
    public ResponseEntity<Page<TransactionResponse>> getTransactions(@PathVariable @Positive(message = "User ID must be positive") Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "transactionDate") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<TransactionResponse> transactions = transactionService.getUserTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }
}
