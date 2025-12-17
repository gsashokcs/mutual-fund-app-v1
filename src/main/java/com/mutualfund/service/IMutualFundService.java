package com.mutualfund.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.entity.Nav;
import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;

/**
 * Service interface for mutual fund management operations. Defines contract for fund creation, NAV
 * updates, and fund retrieval. NAV values are managed separately in the Nav table with historical
 * tracking.
 */
public interface IMutualFundService {

    /**
     * Adds a new mutual fund to the system. NAV should be added separately using the updateNav
     * method.
     *
     * @param request the mutual fund request containing fund name
     * @return MutualFund entity that was created
     * @throws com.mutualfund.exception.BusinessException if fund with the same name already exists
     */
    MutualFund addMutualFund(MutualFundRequest request);

    /**
     * Updates or creates the Net Asset Value (NAV) for a mutual fund for a specific date. If a NAV
     * entry exists for the given date, it updates the value. Otherwise, creates a new entry.
     *
     * @param fundId the ID of the fund to update
     * @param request the NAV update request containing the new NAV value and date
     * @return Nav entity with updated or created NAV
     * @throws com.mutualfund.exception.ResourceNotFoundException if fund is not found
     */
    Nav updateNav(Long fundId, NavUpdateRequest request);

    /**
     * Retrieves all mutual funds in the system.
     *
     * @return List of all MutualFund entities (without NAV data)
     */
    List<MutualFund> getAllMutualFunds();

    /**
     * Retrieves all mutual funds with pagination support.
     *
     * @param pageable the pagination information
     * @return Page of MutualFund entities (without NAV data)
     */
    Page<MutualFund> getAllMutualFunds(Pageable pageable);

    /**
     * Retrieves a mutual fund by its ID.
     *
     * @param fundId the ID of the fund to retrieve
     * @return MutualFund entity (without NAV data - NAV should be queried separately using
     *     NavRepository)
     * @throws com.mutualfund.exception.ResourceNotFoundException if fund is not found
     */
    MutualFund getCurrentMutualFund(Long fundId);

    /**
     * Deletes a mutual fund from the system and soft deletes all associated NAV entries.
     *
     * @param fundId the ID of the fund to delete
     * @throws com.mutualfund.exception.ResourceNotFoundException if fund is not found
     */
    void deleteMutualFund(Long fundId);
}
