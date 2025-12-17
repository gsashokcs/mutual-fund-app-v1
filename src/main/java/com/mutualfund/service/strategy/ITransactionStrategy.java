package com.mutualfund.service.strategy;

import java.math.BigDecimal;

import com.mutualfund.model.entity.Holding;

/**
 * Strategy interface for transaction processing. Implements Strategy pattern to handle different transaction types polymorphically.
 */
public interface ITransactionStrategy {

    /**
     * Processes a transaction and updates the holding accordingly.
     *
     * @param holding
     *            the holding to update
     * @param units
     *            the number of units in the transaction
     * @param nav
     *            the net asset value at transaction time
     * @throws com.mutualfund.exception.BusinessException
     *             if transaction cannot be processed
     */
    void processTransaction(Holding holding, BigDecimal units, BigDecimal nav);

    /**
     * Validates if the transaction can be processed.
     *
     * @param holding
     *            the holding to validate against
     * @param units
     *            the number of units in the transaction
     * @return true if transaction is valid, false otherwise
     */
    boolean validateTransaction(Holding holding, BigDecimal units);

    /**
     * Gets the transaction type name.
     *
     * @return the transaction type name
     */
    String getTransactionType();
}
