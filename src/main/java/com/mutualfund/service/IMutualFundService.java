package com.mutualfund.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mutualfund.model.entity.MutualFund;
import com.mutualfund.model.request.MutualFundRequest;
import com.mutualfund.model.request.NavUpdateRequest;

/**
 * Service interface for mutual fund management operations. Defines contract for fund creation, NAV
 * updates, and fund retrieval.
 */
public interface IMutualFundService {

    /**
     * Adds a new mutual fund to the system with current date's NAV.
     *
     * @param request the mutual fund request containing name and NAV
     * @return MutualFund entity that was created
     * @throws com.mutualfund.exception.BusinessException if fund already exists for the current
     *     date
     */
    MutualFund addMutualFund(MutualFundRequest request);

    /**
     * Updates the Net Asset Value (NAV) for a mutual fund for the current date.
     *
     * @param fundId the ID of the fund to update
     * @param request the NAV update request containing the new NAV value
     * @return MutualFund entity with updated NAV
     * @throws com.mutualfund.exception.ResourceNotFoundException if fund is not found for the
     *     current date
     */
    MutualFund updateNav(Long fundId, NavUpdateRequest request);

    /**
     * Retrieves all mutual funds in the system.
     *
     * @return List of all MutualFund entities
     */
    List<MutualFund> getAllMutualFunds();

    /**
     * Retrieves all mutual funds with pagination support.
     *
     * @param pageable the pagination information
     * @return Page of MutualFund entities
     */
    Page<MutualFund> getAllMutualFunds(Pageable pageable);

    /**
     * Retrieves a mutual fund's current NAV for today's date.
     *
     * @param fundId the ID of the fund to retrieve
     * @return MutualFund entity with current date's NAV
     * @throws com.mutualfund.exception.ResourceNotFoundException if fund is not found for the
     *     current date
     */
    MutualFund getCurrentMutualFund(Long fundId);

    /**
     * Deletes a mutual fund from the system.
     *
     * @param fundId the ID of the fund to delete
     * @throws com.mutualfund.exception.ResourceNotFoundException if fund is not found
     */
    void deleteMutualFund(Long fundId);
}
