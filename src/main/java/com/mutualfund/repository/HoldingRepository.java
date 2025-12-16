package com.mutualfund.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mutualfund.model.entity.Holding;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    /**
     * Finds all holdings for a specific user.
     *
     * @param userId the ID of the user
     * @return List of holdings
     */
    List<Holding> findByUserId(Long userId);

    /**
     * Finds a specific holding for a user and fund combination.
     *
     * @param userId the ID of the user
     * @param fundId the ID of the fund
     * @return Optional containing the holding if found
     */
    Optional<Holding> findByUserIdAndFundId(Long userId, Long fundId);
}
