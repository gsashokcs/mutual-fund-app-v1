package com.mutualfund.service.strategy;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mutualfund.model.entity.Holding;

/** Strategy implementation for buy transactions. Adds units and value to holdings. */
@Component
public class BuyTransactionStrategy implements ITransactionStrategy {

    @Override
    public void processTransaction(Holding holding, BigDecimal units, BigDecimal nav) {
        BigDecimal transactionValue = units.multiply(nav);

        holding.setUnits(holding.getUnits().add(units));
        holding.setTotalValue(holding.getTotalValue().add(transactionValue));
    }

    @Override
    public boolean validateTransaction(Holding holding, BigDecimal units) {
        return units.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String getTransactionType() {
        return "BUY";
    }
}
