package com.mutualfund.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mutualfund.exception.BusinessException;
import com.mutualfund.exception.ErrorCode;
import com.mutualfund.model.entity.Holding;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.entity.Transaction;
import com.mutualfund.model.request.TransactionRequest;
import com.mutualfund.model.response.HoldingResponse;
import com.mutualfund.model.response.TransactionResponse;
import com.mutualfund.repository.HoldingRepository;
import com.mutualfund.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final HoldingRepository holdingRepository;
    private final MutualFundService mutualFundService;

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "holdings", key = "#userId"),
                @CacheEvict(value = "transactions", key = "#userId")
            })
    public TransactionResponse buyUnits(Long userId, TransactionRequest request) {
        log.info(
                "Processing buy transaction for user ID: {}, fund ID: {}",
                userId,
                request.getFundId());

        MutualFund fund = mutualFundService.getCurrentMutualFund(request.getFundId());

        Transaction transaction =
                Transaction.builder()
                        .userId(userId)
                        .fundId(request.getFundId())
                        .units(request.getUnits())
                        .nav(fund.getNav())
                        .type(Transaction.TransactionType.BUY)
                        .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        updateHolding(userId, request.getFundId(), request.getUnits(), fund.getNav(), true);

        log.info("Buy transaction completed successfully: {}", savedTransaction.getTransactionId());
        return mapToTransactionResponse(savedTransaction, fund.getName());
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "holdings", key = "#userId"),
                @CacheEvict(value = "transactions", key = "#userId")
            })
    public TransactionResponse redeemUnits(Long userId, TransactionRequest request) {
        log.info(
                "Processing redeem transaction for user ID: {}, fund ID: {}",
                userId,
                request.getFundId());

        MutualFund fund = mutualFundService.getCurrentMutualFund(request.getFundId());

        Holding holding =
                holdingRepository
                        .findByUserIdAndFundId(userId, request.getFundId())
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                ErrorCode.HOLDING_NOT_FOUND,
                                                "No holdings found for this fund"));

        if (holding.getUnits().compareTo(request.getUnits()) < 0) {
            throw new BusinessException(
                    ErrorCode.INSUFFICIENT_UNITS,
                    "Insufficient units. Available: " + holding.getUnits());
        }

        Transaction transaction =
                Transaction.builder()
                        .userId(userId)
                        .fundId(request.getFundId())
                        .units(request.getUnits())
                        .nav(fund.getNav())
                        .type(Transaction.TransactionType.REDEEM)
                        .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        updateHolding(userId, request.getFundId(), request.getUnits(), fund.getNav(), false);

        log.info(
                "Redeem transaction completed successfully: {}",
                savedTransaction.getTransactionId());
        return mapToTransactionResponse(savedTransaction, fund.getName());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "holdings", key = "#userId")
    public List<HoldingResponse> getUserHoldings(Long userId) {
        log.info("Fetching holdings for user ID: {}", userId);

        List<Holding> holdings = holdingRepository.findByUserId(userId);

        return holdings.stream()
                .filter(holding -> holding.getUnits().compareTo(BigDecimal.ZERO) > 0)
                .map(this::mapToHoldingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions", key = "#userId")
    public List<TransactionResponse> getUserTransactions(Long userId) {
        log.info("Fetching transactions for user ID: {}", userId);

        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        return transactions.stream().map(t -> mapToTransactionResponse(t, "")).toList();
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactions(Long userId, Pageable pageable) {
        log.info(
                "Fetching transactions for user ID: {} with pagination: page {}, size {}",
                userId,
                pageable.getPageNumber(),
                pageable.getPageSize());

        return transactionRepository
                .findByUserId(userId, pageable)
                .map(t -> mapToTransactionResponse(t, ""));
    }

    private void updateHolding(
            Long userId, Long fundId, BigDecimal units, BigDecimal nav, boolean isBuy) {
        Holding holding =
                holdingRepository
                        .findByUserIdAndFundId(userId, fundId)
                        .orElseGet(
                                () ->
                                        Holding.builder()
                                                .userId(userId)
                                                .fundId(fundId)
                                                .units(BigDecimal.ZERO)
                                                .totalValue(BigDecimal.ZERO)
                                                .build());

        BigDecimal transactionValue = units.multiply(nav);
        BigDecimal unitsChange = isBuy ? units : units.negate();
        BigDecimal valueChange = isBuy ? transactionValue : transactionValue.negate();

        holding.setUnits(holding.getUnits().add(unitsChange));
        holding.setTotalValue(holding.getTotalValue().add(valueChange));

        holdingRepository.save(holding);
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction, String fundName) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUserId())
                .fundId(transaction.getFundId())
                .fundName(fundName)
                .units(transaction.getUnits())
                .nav(transaction.getNav())
                .type(transaction.getType().name())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }

    private HoldingResponse mapToHoldingResponse(Holding holding) {
        MutualFund currentFund = mutualFundService.getCurrentMutualFund(holding.getFundId());
        BigDecimal currentValue =
                holding.getUnits().multiply(currentFund.getNav()).setScale(2, RoundingMode.HALF_UP);

        return HoldingResponse.builder()
                .fundId(holding.getFundId())
                .fundName(currentFund.getName())
                .units(holding.getUnits())
                .currentNav(currentFund.getNav())
                .totalValue(currentValue)
                .build();
    }
}
