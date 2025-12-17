package com.mutualfund.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the Net Asset Value (NAV) for a mutual fund on a specific date. Tracks
 * historical NAV values for each fund with soft delete support.
 *
 * @author Mutual Fund Management System
 * @version 1.0
 */
@Entity
@Schema(description = "Historical NAV entry for a mutual fund on a specific date")
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
    @Schema(description = "Unique identifier for the NAV entry", example = "1")
    private Long navId;

    @NotNull(message = "Fund ID is required")
    @Column(name = "fund_id", nullable = false)
    @Schema(description = "Foreign key to the mutual fund", example = "1")
    private Long fundId;

    @NotNull(message = "NAV is required")
    @DecimalMin(value = "0.01", message = "NAV must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Net Asset Value per unit", example = "155.50")
    private BigDecimal nav;

    @NotNull(message = "NAV date is required")
    @Column(name = "nav_date", nullable = false)
    @Schema(description = "Date of the NAV value", example = "2025-12-16")
    private LocalDate navDate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;
}
