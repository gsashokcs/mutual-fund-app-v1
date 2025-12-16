package com.mutualfund.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mutualfund.model.entity.Nav;

/**
 * Repository interface for Nav entity operations.
 * Provides methods to access and manage NAV history data.
 *
 * @author Mutual Fund Management System
 * @version 1.0
 */
@Repository
public interface NavRepository extends JpaRepository<Nav, Long> {

    /**
     * Finds a NAV entry by fund ID and date.
     *
     * @param fundId the ID of the mutual fund
     * @param navDate the NAV date
     * @return Optional containing the Nav if found
     */
    Optional<Nav> findByFundIdAndNavDateAndDeletedFalse(Long fundId, LocalDate navDate);

    /**
     * Finds all NAV entries for a specific fund.
     *
     * @param fundId the ID of the mutual fund
     * @return List of Nav entries
     */
    List<Nav> findByFundIdAndDeletedFalseOrderByNavDateDesc(Long fundId);

    /**
     * Finds the latest NAV entry for a specific fund.
     *
     * @param fundId the ID of the mutual fund
     * @return Optional containing the latest Nav if found
     */
    Optional<Nav> findTopByFundIdAndDeletedFalseOrderByNavDateDesc(Long fundId);

    /**
     * Soft deletes all NAV entries for a specific fund.
     *
     * @param fundId the ID of the mutual fund
     * @return number of entries updated
     */
    List<Nav> findByFundIdAndDeletedFalse(Long fundId);
}
