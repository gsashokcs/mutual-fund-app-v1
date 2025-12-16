package com.mutualfund.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the Net Asset Value (NAV) for a mutual fund on a specific date.
 * Tracks historical NAV values for each fund with soft delete support.
 *
 * @author Mutual Fund Management System
 * @version 1.0
 */
@Entity
@Table(
        name = "nav_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fund_id", "nav_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nav {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long navId;

    @NotNull(message = "Fund ID is required")
    @Column(name = "fund_id", nullable = false)
    private Long fundId;

    @NotNull(message = "NAV is required")
    @DecimalMin(value = "0.01", message = "NAV must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nav;

    @NotNull(message = "NAV date is required")
    @Column(name = "nav_date", nullable = false)
    private LocalDate navDate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;
}
