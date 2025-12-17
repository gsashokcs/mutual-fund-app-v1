package com.mutualfund.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mutualfund.model.entity.MutualFund;

@Repository
public interface MutualFundRepository extends JpaRepository<MutualFund, Long> {

    /**
     * Finds a mutual fund by name.
     *
     * @param name
     *            the name of the mutual fund
     * @return Optional containing the mutual fund if found
     */
    Optional<MutualFund> findByName(String name);
}
