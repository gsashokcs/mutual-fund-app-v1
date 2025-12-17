package com.mutualfund.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mutualfund.exception.BusinessException;
import com.mutualfund.exception.ErrorCode;
import com.mutualfund.exception.ResourceNotFoundException;
import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.entity.Nav;
import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;
import com.mutualfund.repository.MutualFundRepository;
import com.mutualfund.repository.NavRepository;

@Service
public class MutualFundService implements IMutualFundService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MutualFundService.class);

    private final MutualFundRepository mutualFundRepository;
    private final NavRepository navRepository;

    public MutualFundService(MutualFundRepository mutualFundRepository, NavRepository navRepository) {
        this.mutualFundRepository = mutualFundRepository;
        this.navRepository = navRepository;
    }

    /**
     * Adds a new mutual fund to the system. NAV should be added separately using the updateNav method.
     *
     * @param request
     *            the mutual fund request containing fund name
     * @return MutualFund entity that was created
     * @throws BusinessException
     *             if fund with the same name already exists
     */
    @Transactional
    public MutualFund addMutualFund(MutualFundRequest request) {
        log.info("Adding new mutual fund: {}", request.getName());

        mutualFundRepository.findByName(request.getName()).ifPresent(existing -> {
            throw new BusinessException(ErrorCode.DUPLICATE_FUND, "Mutual fund already exists: " + request.getName());
        });

        MutualFund fund = MutualFund.builder().name(request.getName()).build();

        MutualFund savedFund = mutualFundRepository.save(fund);
        log.info("Mutual fund added successfully with ID: {}", savedFund.getFundId());

        return savedFund;
    }

    /**
     * Updates the Net Asset Value (NAV) for a mutual fund for a specific date. If a NAV entry exists for the given date, it updates the value. Otherwise, creates a new entry.
     *
     * @param fundId
     *            the ID of the fund to update
     * @param request
     *            the NAV update request containing the new NAV value and date
     * @return Nav entity with updated or created NAV
     * @throws ResourceNotFoundException
     *             if fund is not found
     */
    @Transactional
    public Nav updateNav(Long fundId, NavUpdateRequest request) {
        log.info("Updating NAV for fund ID: {} on date: {}", fundId, request.getNavDate());

        // Verify fund exists
        if (!mutualFundRepository.existsById(fundId)) {
            throw new ResourceNotFoundException(ErrorCode.MUTUAL_FUND_NOT_FOUND, "Mutual fund not found with ID: " + fundId);
        }

        // Find existing NAV entry or create new one
        Nav nav = navRepository.findByFundIdAndNavDateAndDeletedFalse(fundId, request.getNavDate()).orElse(Nav.builder().fundId(fundId).navDate(request.getNavDate()).build());

        nav.setNav(request.getNav());
        Nav savedNav = navRepository.save(nav);

        log.info("NAV updated successfully for fund ID: {} on {}", fundId, request.getNavDate());
        return savedNav;
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
     * @param pageable
     *            the pagination information
     * @return Page of MutualFund entities
     */
    @Transactional(readOnly = true)
    public Page<MutualFund> getAllMutualFunds(Pageable pageable) {
        log.info("Fetching mutual funds with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return mutualFundRepository.findAll(pageable);
    }

    /**
     * Retrieves a mutual fund by its ID.
     *
     * @param fundId
     *            the ID of the fund to retrieve
     * @return MutualFund entity (without NAV data - NAV should be queried separately using NavRepository)
     * @throws ResourceNotFoundException
     *             if fund is not found
     */
    @Transactional(readOnly = true)
    public MutualFund getCurrentMutualFund(Long fundId) {
        log.info("Fetching mutual fund by ID: {}", fundId);

        return mutualFundRepository.findById(fundId).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.MUTUAL_FUND_NOT_FOUND, "Mutual fund not found with ID: " + fundId));
    }

    /**
     * Deletes a mutual fund from the system and soft deletes all associated NAV entries.
     *
     * @param fundId
     *            the ID of the fund to delete
     * @throws ResourceNotFoundException
     *             if fund is not found
     */
    @Transactional
    public void deleteMutualFund(Long fundId) {
        log.info("Deleting mutual fund with ID: {}", fundId);

        if (!mutualFundRepository.existsById(fundId)) {
            throw new ResourceNotFoundException(ErrorCode.MUTUAL_FUND_NOT_FOUND, "Mutual fund not found with ID: " + fundId);
        }

        // Soft delete all NAV entries for this fund
        List<Nav> navEntries = navRepository.findByFundIdAndDeletedFalse(fundId);
        navEntries.forEach(nav -> nav.setDeleted(true));
        navRepository.saveAll(navEntries);
        log.info("Soft deleted {} NAV entries for fund ID: {}", navEntries.size(), fundId);

        // Delete the mutual fund
        mutualFundRepository.deleteById(fundId);
        log.info("Mutual fund deleted successfully: {}", fundId);
    }
}
