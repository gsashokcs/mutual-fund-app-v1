package com.mutualfund.service.strategy;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mutualfund.exception.BusinessException;
import com.mutualfund.exception.ErrorCode;
import com.mutualfund.model.entity.Holding;

/**
 * Strategy implementation for redeem transactions. Deducts units and value from holdings with validation.
 */
@Component
public class RedeemTransactionStrategy implements ITransactionStrategy {

    @Override
    public void processTransaction(Holding holding, BigDecimal units, BigDecimal nav) {
        if (!validateTransaction(holding, units)) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_UNITS, "Insufficient units. Available: " + holding.getUnits());
        }

        BigDecimal transactionValue = units.multiply(nav);

        holding.setUnits(holding.getUnits().subtract(units));
        holding.setTotalValue(holding.getTotalValue().subtract(transactionValue));
    }

    @Override
    public boolean validateTransaction(Holding holding, BigDecimal units) {
        return units.compareTo(BigDecimal.ZERO) > 0 && holding.getUnits().compareTo(units) >= 0;
    }

    @Override
    public String getTransactionType() {
        return "REDEEM";
    }
}
