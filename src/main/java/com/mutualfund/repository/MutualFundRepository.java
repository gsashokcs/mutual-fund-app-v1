package com.mutualfund.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mutualfund.model.entity.MutualFund;

@Repository
public interface MutualFundRepository extends JpaRepository<MutualFund, Long> {

    /**
     * Finds a mutual fund by name and NAV date.
     *
     * @param name the name of the mutual fund
     * @param navDate the NAV date to search for
     * @return Optional containing the mutual fund if found
     */
    Optional<MutualFund> findByNameAndNavDate(String name, LocalDate navDate);

    /**
     * Finds a mutual fund by fund ID and NAV date.
     *
     * @param fundId the ID of the fund
     * @param navDate the NAV date to search for
     * @return Optional containing the mutual fund if found
     */
    Optional<MutualFund> findByFundIdAndNavDate(Long fundId, LocalDate navDate);
}
