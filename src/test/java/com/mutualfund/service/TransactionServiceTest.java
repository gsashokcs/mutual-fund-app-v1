package com.mutualfund.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mutualfund.exception.BusinessException;
import com.mutualfund.model.entity.Holding;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.entity.Nav;
import com.mutualfund.model.entity.Transaction;
import com.mutualfund.model.request.TransactionRequest;
import com.mutualfund.model.response.TransactionResponse;
import com.mutualfund.repository.HoldingRepository;
import com.mutualfund.repository.NavRepository;
import com.mutualfund.repository.TransactionRepository;
import com.mutualfund.service.strategy.TransactionStrategyFactory;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;

    @Mock private HoldingRepository holdingRepository;

    @Mock private MutualFundService mutualFundService;

    @Mock private NavRepository navRepository;

    @Mock private TransactionStrategyFactory strategyFactory;

    @Mock private SecurityService securityService;

    @InjectMocks private TransactionService transactionService;

    private MutualFund testFund;
    private Nav testNav;
    private Holding testHolding;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testFund = new MutualFund();
        testFund.setFundId(1L);
        testFund.setName("Test Fund");

        testNav = new Nav();
        testNav.setNavId(1L);
        testNav.setFundId(1L);
        testNav.setNav(new BigDecimal("100.00"));
        testNav.setNavDate(LocalDate.now());
        testNav.setDeleted(false);

        testHolding = new Holding();
        testHolding.setId(1L);
        testHolding.setUserId(1L);
        testHolding.setFundId(1L);
        testHolding.setUnits(new BigDecimal("10.0000"));
        testHolding.setTotalValue(new BigDecimal("1000.00"));

        testTransaction = new Transaction();
        testTransaction.setTransactionId(1L);
        testTransaction.setUserId(1L);
        testTransaction.setFundId(1L);
        testTransaction.setUnits(new BigDecimal("5.0000"));
        testTransaction.setNav(new BigDecimal("100.00"));
        testTransaction.setType(Transaction.TransactionType.BUY);
    }

    @Test
    void buyUnitsSuccess() {
        TransactionRequest request = new TransactionRequest(1L, new BigDecimal("5.0000"));

        doNothing().when(securityService).validateUserAccess(1L);
        when(mutualFundService.getCurrentMutualFund(1L)).thenReturn(testFund);
        when(navRepository.findTopByFundIdAndDeletedFalseOrderByNavDateDesc(1L))
                .thenReturn(Optional.of(testNav));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(holdingRepository.findByUserIdAndFundId(1L, 1L)).thenReturn(Optional.empty());
        when(strategyFactory.getStrategy("BUY"))
                .thenReturn(mock(com.mutualfund.service.strategy.ITransactionStrategy.class));
        when(holdingRepository.save(any(Holding.class))).thenReturn(testHolding);

        TransactionResponse response = transactionService.buyUnits(1L, request);

        assertNotNull(response);
        assertEquals(testTransaction.getTransactionId(), response.getTransactionId());
        assertEquals(Transaction.TransactionType.BUY.name(), response.getType());
        verify(transactionRepository).save(any(Transaction.class));
        verify(holdingRepository).save(any(Holding.class));
    }

    @Test
    void redeemUnitsSuccess() {
        TransactionRequest request = new TransactionRequest(1L, new BigDecimal("3.0000"));

        doNothing().when(securityService).validateUserAccess(1L);
        when(mutualFundService.getCurrentMutualFund(1L)).thenReturn(testFund);
        when(navRepository.findTopByFundIdAndDeletedFalseOrderByNavDateDesc(1L))
                .thenReturn(Optional.of(testNav));
        when(holdingRepository.findByUserIdAndFundId(1L, 1L)).thenReturn(Optional.of(testHolding));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(strategyFactory.getStrategy("REDEEM"))
                .thenReturn(mock(com.mutualfund.service.strategy.ITransactionStrategy.class));
        when(holdingRepository.save(any(Holding.class))).thenReturn(testHolding);

        TransactionResponse response = transactionService.redeemUnits(1L, request);

        assertNotNull(response);
        verify(transactionRepository).save(any(Transaction.class));
        verify(holdingRepository).save(any(Holding.class));
    }

    @Test
    void redeemUnitsNoHoldingsThrowsException() {
        TransactionRequest request = new TransactionRequest(1L, new BigDecimal("3.0000"));

        doNothing().when(securityService).validateUserAccess(1L);
        when(mutualFundService.getCurrentMutualFund(1L)).thenReturn(testFund);
        when(navRepository.findTopByFundIdAndDeletedFalseOrderByNavDateDesc(1L))
                .thenReturn(Optional.of(testNav));
        when(holdingRepository.findByUserIdAndFundId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> transactionService.redeemUnits(1L, request));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void redeemUnitsInsufficientUnitsThrowsException() {
        TransactionRequest request = new TransactionRequest(1L, new BigDecimal("15.0000"));

        doNothing().when(securityService).validateUserAccess(1L);
        when(mutualFundService.getCurrentMutualFund(1L)).thenReturn(testFund);
        when(navRepository.findTopByFundIdAndDeletedFalseOrderByNavDateDesc(1L))
                .thenReturn(Optional.of(testNav));
        when(holdingRepository.findByUserIdAndFundId(1L, 1L)).thenReturn(Optional.of(testHolding));

        assertThrows(BusinessException.class, () -> transactionService.redeemUnits(1L, request));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
