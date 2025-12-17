package com.mutualfund.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mutualfund.model.request.TransactionRequest;
import com.mutualfund.model.response.HoldingResponse;
import com.mutualfund.model.response.TransactionResponse;

/**
 * Service interface for transaction and holdings management. Defines contract for buy/redeem operations and portfolio viewing.
 */
public interface ITransactionService {

    /**
     * Processes a buy transaction for mutual fund units.
     *
     * @param userId
     *            the ID of the user making the purchase
     * @param request
     *            the transaction request containing fund ID and units
     * @return TransactionResponse containing transaction details
     * @throws com.mutualfund.exception.ResourceNotFoundException
     *             if mutual fund is not found
     */
    TransactionResponse buyUnits(Long userId, TransactionRequest request);

    /**
     * Processes a redemption transaction for mutual fund units.
     *
     * @param userId
     *            the ID of the user redeeming units
     * @param request
     *            the transaction request containing fund ID and units
     * @return TransactionResponse containing transaction details
     * @throws com.mutualfund.exception.BusinessException
     *             if holdings not found or insufficient units
     * @throws com.mutualfund.exception.ResourceNotFoundException
     *             if mutual fund is not found
     */
    TransactionResponse redeemUnits(Long userId, TransactionRequest request);

    /**
     * Retrieves all mutual fund holdings for a user with current values.
     *
     * @param userId
     *            the ID of the user
     * @return List of HoldingResponse with current NAV and values
     */
    List<HoldingResponse> getUserHoldings(Long userId);

    /**
     * Retrieves all transactions for a user.
     *
     * @param userId
     *            the ID of the user
     * @return List of TransactionResponse containing transaction history
     */
    List<TransactionResponse> getUserTransactions(Long userId);

    /**
     * Retrieves all transactions for a user with pagination support.
     *
     * @param userId
     *            the ID of the user
     * @param pageable
     *            the pagination information
     * @return Page of TransactionResponse containing transaction history
     */
    Page<TransactionResponse> getUserTransactions(Long userId, Pageable pageable);
}
