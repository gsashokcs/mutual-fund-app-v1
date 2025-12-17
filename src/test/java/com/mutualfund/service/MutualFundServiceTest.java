package com.mutualfund.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mutualfund.exception.BusinessException;
import com.mutualfund.exception.ResourceNotFoundException;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.entity.Nav;
import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;
import com.mutualfund.repository.MutualFundRepository;
import com.mutualfund.repository.NavRepository;

@ExtendWith(MockitoExtension.class)
class MutualFundServiceTest {

    @Mock private MutualFundRepository mutualFundRepository;
    @Mock private NavRepository navRepository;

    @InjectMocks private MutualFundService mutualFundService;

    private MutualFund testFund;
    private MutualFundRequest fundRequest;

    @BeforeEach
    void setUp() {
        testFund = new MutualFund();
        testFund.setFundId(1L);
        testFund.setName("Test Fund");

        fundRequest = new MutualFundRequest();
        fundRequest.setName("New Fund");
    }

    @Test
    void addMutualFundSuccess() {
        when(mutualFundRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(mutualFundRepository.save(any(MutualFund.class))).thenReturn(testFund);

        MutualFund result = mutualFundService.addMutualFund(fundRequest);

        assertNotNull(result);
        assertEquals(testFund.getName(), result.getName());
        verify(mutualFundRepository).save(any(MutualFund.class));
    }

    @Test
    void addMutualFundAlreadyExistsThrowsException() {
        when(mutualFundRepository.findByName(anyString())).thenReturn(Optional.of(testFund));

        assertThrows(BusinessException.class, () -> mutualFundService.addMutualFund(fundRequest));
        verify(mutualFundRepository, never()).save(any(MutualFund.class));
    }

    @Test
    void updateNavSuccess() {
        NavUpdateRequest navRequest =
                new NavUpdateRequest(new BigDecimal("120.00"), LocalDate.now());
        Nav testNav = new Nav();
        testNav.setNavId(1L);
        testNav.setFundId(1L);
        testNav.setNav(new BigDecimal("120.00"));
        testNav.setNavDate(LocalDate.now());

        when(mutualFundRepository.existsById(1L)).thenReturn(true);
        when(navRepository.findByFundIdAndNavDateAndDeletedFalse(1L, LocalDate.now()))
                .thenReturn(Optional.empty());
        when(navRepository.save(any(Nav.class))).thenReturn(testNav);

        Nav result = mutualFundService.updateNav(1L, navRequest);

        assertNotNull(result);
        verify(navRepository).save(any(Nav.class));
    }

    @Test
    void updateNavNotFoundThrowsException() {
        NavUpdateRequest navRequest =
                new NavUpdateRequest(new BigDecimal("120.00"), LocalDate.now());
        when(mutualFundRepository.existsById(1L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class, () -> mutualFundService.updateNav(1L, navRequest));
    }

    @Test
    void getAllMutualFundsSuccess() {
        List<MutualFund> funds = Arrays.asList(testFund);
        when(mutualFundRepository.findAll()).thenReturn(funds);

        List<MutualFund> result = mutualFundService.getAllMutualFunds();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mutualFundRepository).findAll();
    }

    @Test
    void getCurrentMutualFundSuccess() {
        when(mutualFundRepository.findById(1L)).thenReturn(Optional.of(testFund));

        MutualFund result = mutualFundService.getCurrentMutualFund(1L);

        assertNotNull(result);
        assertEquals(testFund.getFundId(), result.getFundId());
    }

    @Test
    void getCurrentMutualFundNotFoundThrowsException() {
        when(mutualFundRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class, () -> mutualFundService.getCurrentMutualFund(1L));
    }

    @Test
    void deleteMutualFundSuccess() {
        when(mutualFundRepository.existsById(1L)).thenReturn(true);
        when(navRepository.findByFundIdAndDeletedFalse(1L)).thenReturn(Arrays.asList());

        mutualFundService.deleteMutualFund(1L);

        verify(mutualFundRepository).deleteById(1L);
    }

    @Test
    void deleteMutualFundNotFoundThrowsException() {
        when(mutualFundRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> mutualFundService.deleteMutualFund(1L));
        verify(mutualFundRepository, never()).deleteById(anyLong());
    }
}
