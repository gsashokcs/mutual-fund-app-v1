package com.mutualfund.service.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Factory for retrieving transaction strategies. Implements Factory pattern to provide appropriate strategy based on transaction type.
 */
@Component
public class TransactionStrategyFactory {

    private final Map<String, ITransactionStrategy> strategies = new HashMap<>();

    /**
     * Constructor that registers all available transaction strategies.
     *
     * @param strategyList
     *            list of all transaction strategy implementations
     */
    public TransactionStrategyFactory(List<ITransactionStrategy> strategyList) {
        for (ITransactionStrategy strategy : strategyList) {
            strategies.put(strategy.getTransactionType(), strategy);
        }
    }

    /**
     * Gets the appropriate strategy for the given transaction type.
     *
     * @param transactionType
     *            the type of transaction (BUY, REDEEM)
     * @return the corresponding transaction strategy
     * @throws IllegalArgumentException
     *             if transaction type is not supported
     */
    public ITransactionStrategy getStrategy(String transactionType) {
        ITransactionStrategy strategy = strategies.get(transactionType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported transaction type: " + transactionType);
        }
        return strategy;
    }
}
