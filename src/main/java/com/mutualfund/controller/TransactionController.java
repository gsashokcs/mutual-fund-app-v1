package com.mutualfund.controller;

import com.mutualfund.model.response.HoldingResponse;
import com.mutualfund.model.request.TransactionRequest;
import com.mutualfund.model.response.TransactionResponse;
import com.mutualfund.service.TransactionService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "basicAuth")
@Tag(name = "Transactions", description = "User transaction and holdings management endpoints")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/buy")
    @Operation(summary = "Buy units", description = "Purchases mutual fund units at current day's NAV")
    public ResponseEntity<TransactionResponse> buyUnits(
            @PathVariable @Positive(message = "User ID must be positive") Long userId,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.buyUnits(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/redeem")
    @Operation(summary = "Redeem units", description = "Redeems mutual fund units at current day's NAV")
    public ResponseEntity<TransactionResponse> redeemUnits(
            @PathVariable @Positive(message = "User ID must be positive") Long userId,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.redeemUnits(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/holdings")
    @Operation(summary = "View holdings", description = "Retrieves all mutual fund holdings for a user with current values")
    public ResponseEntity<List<HoldingResponse>> getHoldings(
            @PathVariable @Positive(message = "User ID must be positive") Long userId) {
        List<HoldingResponse> holdings = transactionService.getUserHoldings(userId);
        return ResponseEntity.ok(holdings);
    }

    @GetMapping("/transactions")
    @Operation(summary = "View transactions", description = "Retrieves transaction history for a user with pagination")
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @PathVariable @Positive(message = "User ID must be positive") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<TransactionResponse> transactions = transactionService.getUserTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }
}
