package com.mutualfund.repository;

import com.mutualfund.model.entity.MutualFund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MutualFundRepository extends JpaRepository<MutualFund, Long> {

    Optional<MutualFund> findByNameAndNavDate(String name, LocalDate navDate);

    Optional<MutualFund> findByFundIdAndNavDate(Long fundId, LocalDate navDate);
}
