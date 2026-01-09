package com.mutualfund.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mutualfund.model.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions for a specific user.
     *
     * @param userId
     *            the ID of the user
     * @return List of transactions
     */
    List<Transaction> findByUserId(Long userId);

    /**
     * Finds all transactions for a specific user with pagination.
     *
     * @param userId
     *            the ID of the user
     * @param pageable
     *            the pagination information
     * @return Page of transactions
     */
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds all transactions for a specific user and fund.
     *
     * @param userId
     *            the ID of the user
     * @param fundId
     *            the ID of the fund
     * @return List of transactions
     */
    List<Transaction> findByUserIdAndFundId(Long userId, Long fundId);
}
