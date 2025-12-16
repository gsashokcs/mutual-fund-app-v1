package com.mutualfund.service;

import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;
import com.mutualfund.exception.BusinessException;
import com.mutualfund.exception.ResourceNotFoundException;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.repository.MutualFundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MutualFundServiceTest {

    @Mock
    private MutualFundRepository mutualFundRepository;

    @InjectMocks
    private MutualFundService mutualFundService;

    private MutualFund testFund;
    private MutualFundRequest fundRequest;

    @BeforeEach
    void setUp() {
        testFund = new MutualFund();
        testFund.setFundId(1L);
        testFund.setName("Test Fund");
        testFund.setNav(new BigDecimal("100.50"));
        testFund.setNavDate(LocalDate.now());

        fundRequest = new MutualFundRequest();
        fundRequest.setName("New Fund");
        fundRequest.setNav(new BigDecimal("150.00"));
    }

    @Test
    void addMutualFund_Success() {
        when(mutualFundRepository.findByNameAndNavDate(anyString(), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(mutualFundRepository.save(any(MutualFund.class))).thenReturn(testFund);

        MutualFund result = mutualFundService.addMutualFund(fundRequest);

        assertNotNull(result);
        assertEquals(testFund.getName(), result.getName());
        verify(mutualFundRepository).save(any(MutualFund.class));
    }

    @Test
    void addMutualFund_AlreadyExists_ThrowsException() {
        when(mutualFundRepository.findByNameAndNavDate(anyString(), any(LocalDate.class)))
                .thenReturn(Optional.of(testFund));

        assertThrows(BusinessException.class, () -> mutualFundService.addMutualFund(fundRequest));
        verify(mutualFundRepository, never()).save(any(MutualFund.class));
    }

    @Test
    void updateNav_Success() {
        NavUpdateRequest navRequest = new NavUpdateRequest(new BigDecimal("120.00"));
        when(mutualFundRepository.findByFundIdAndNavDate(1L, LocalDate.now()))
                .thenReturn(Optional.of(testFund));
        when(mutualFundRepository.save(any(MutualFund.class))).thenReturn(testFund);

        MutualFund result = mutualFundService.updateNav(1L, navRequest);

        assertNotNull(result);
        verify(mutualFundRepository).save(any(MutualFund.class));
    }

    @Test
    void updateNav_NotFound_ThrowsException() {
        NavUpdateRequest navRequest = new NavUpdateRequest(new BigDecimal("120.00"));
        when(mutualFundRepository.findByFundIdAndNavDate(1L, LocalDate.now()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mutualFundService.updateNav(1L, navRequest));
    }

    @Test
    void getAllMutualFunds_Success() {
        List<MutualFund> funds = Arrays.asList(testFund);
        when(mutualFundRepository.findAll()).thenReturn(funds);

        List<MutualFund> result = mutualFundService.getAllMutualFunds();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mutualFundRepository).findAll();
    }

    @Test
    void getCurrentMutualFund_Success() {
        when(mutualFundRepository.findByFundIdAndNavDate(1L, LocalDate.now()))
                .thenReturn(Optional.of(testFund));

        MutualFund result = mutualFundService.getCurrentMutualFund(1L);

        assertNotNull(result);
        assertEquals(testFund.getFundId(), result.getFundId());
    }

    @Test
    void getCurrentMutualFund_NotFound_ThrowsException() {
        when(mutualFundRepository.findByFundIdAndNavDate(1L, LocalDate.now()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mutualFundService.getCurrentMutualFund(1L));
    }

    @Test
    void deleteMutualFund_Success() {
        when(mutualFundRepository.existsById(1L)).thenReturn(true);

        mutualFundService.deleteMutualFund(1L);

        verify(mutualFundRepository).deleteById(1L);
    }

    @Test
    void deleteMutualFund_NotFound_ThrowsException() {
        when(mutualFundRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> mutualFundService.deleteMutualFund(1L));
        verify(mutualFundRepository, never()).deleteById(anyLong());
    }
}
