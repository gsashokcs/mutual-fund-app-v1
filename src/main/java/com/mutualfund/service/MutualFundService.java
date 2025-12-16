package com.mutualfund.service;

import java.time.LocalDate;
import java.util.List;

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
public class MutualFundService implements IMutualFundService {

    private final MutualFundRepository mutualFundRepository;

    /**
     * Adds a new mutual fund to the system with current date's NAV.
     *
     * @param request the mutual fund request containing name and NAV
     * @return MutualFund entity that was created
     * @throws BusinessException if fund already exists for the current date
     */
    @Transactional
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

    /**
     * Updates the Net Asset Value (NAV) for a mutual fund for the current date.
     *
     * @param fundId the ID of the fund to update
     * @param request the NAV update request containing the new NAV value
     * @return MutualFund entity with updated NAV
     * @throws ResourceNotFoundException if fund is not found for the current date
     */
    @Transactional
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

    /**
     * Retrieves all mutual funds in the system.
     *
     * @return List of all MutualFund entities
     */
    @Transactional(readOnly = true)
    public List<MutualFund> getAllMutualFunds() {
        log.info("Fetching all mutual funds");
        return mutualFundRepository.findAll();
    }

    /**
     * Retrieves all mutual funds with pagination support.
     *
     * @param pageable the pagination information
     * @return Page of MutualFund entities
     */
    @Transactional(readOnly = true)
    public Page<MutualFund> getAllMutualFunds(Pageable pageable) {
        log.info(
                "Fetching mutual funds with pagination: page {}, size {}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        return mutualFundRepository.findAll(pageable);
    }

    /**
     * Retrieves a mutual fund's current NAV for today's date.
     *
     * @param fundId the ID of the fund to retrieve
     * @return MutualFund entity with current date's NAV
     * @throws ResourceNotFoundException if fund is not found for the current date
     */
    @Transactional(readOnly = true)
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

    /**
     * Deletes a mutual fund from the system.
     *
     * @param fundId the ID of the fund to delete
     * @throws ResourceNotFoundException if fund is not found
     */
    @Transactional
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
