package com.mutualfund.service;

import java.time.LocalDate;
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
import com.mutualfund.exception.ResourceNotFoundException;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;
import com.mutualfund.repository.MutualFundRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MutualFundService {

    private final MutualFundRepository mutualFundRepository;

    @Transactional
    @CacheEvict(value = "allMutualFunds", allEntries = true)
    public MutualFund addMutualFund(MutualFundRequest request) {
        log.info("Adding new mutual fund: {}", request.getName());

        LocalDate today = LocalDate.now();

        mutualFundRepository
                .findByNameAndNavDate(request.getName(), today)
                .ifPresent(
                        existing -> {
                            throw new BusinessException(
                                    ErrorCode.DUPLICATE_FUND,
                                    "Mutual fund already exists for today: " + request.getName());
                        });

        MutualFund fund =
                MutualFund.builder()
                        .name(request.getName())
                        .nav(request.getNav())
                        .navDate(today)
                        .build();

        MutualFund savedFund = mutualFundRepository.save(fund);
        log.info("Mutual fund added successfully with ID: {}", savedFund.getFundId());

        return savedFund;
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "mutualFunds", key = "#fundId"),
                @CacheEvict(value = "allMutualFunds", allEntries = true),
                @CacheEvict(value = "holdings", allEntries = true)
            })
    public MutualFund updateNav(Long fundId, NavUpdateRequest request) {
        log.info("Updating NAV for fund ID: {}", fundId);

        LocalDate today = LocalDate.now();
        MutualFund fund =
                mutualFundRepository
                        .findByFundIdAndNavDate(fundId, today)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ErrorCode.MUTUAL_FUND_NOT_FOUND,
                                                "Mutual fund not found with ID: "
                                                        + fundId
                                                        + " for current date"));

        fund.setNav(request.getNav());
        MutualFund updatedFund = mutualFundRepository.save(fund);

        log.info("NAV updated successfully for fund ID: {}", fundId);
        return updatedFund;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allMutualFunds", key = "'all'")
    public List<MutualFund> getAllMutualFunds() {
        log.info("Fetching all mutual funds");
        return mutualFundRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<MutualFund> getAllMutualFunds(Pageable pageable) {
        log.info(
                "Fetching mutual funds with pagination: page {}, size {}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        return mutualFundRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "mutualFunds", key = "#fundId")
    public MutualFund getCurrentMutualFund(Long fundId) {
        log.info("Fetching mutual fund by ID: {} for current date", fundId);

        LocalDate today = LocalDate.now();
        return mutualFundRepository
                .findByFundIdAndNavDate(fundId, today)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        ErrorCode.MUTUAL_FUND_NOT_FOUND,
                                        "Mutual fund not found with ID: "
                                                + fundId
                                                + " for current date"));
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "mutualFunds", key = "#fundId"),
                @CacheEvict(value = "allMutualFunds", allEntries = true),
                @CacheEvict(value = "holdings", allEntries = true)
            })
    public void deleteMutualFund(Long fundId) {
        log.info("Deleting mutual fund with ID: {}", fundId);

        if (!mutualFundRepository.existsById(fundId)) {
            throw new ResourceNotFoundException(
                    ErrorCode.MUTUAL_FUND_NOT_FOUND, "Mutual fund not found with ID: " + fundId);
        }

        mutualFundRepository.deleteById(fundId);
        log.info("Mutual fund deleted successfully: {}", fundId);
    }
}
