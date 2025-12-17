package com.mutualfund.model.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "holdings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "fund_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Fund ID is required")
    @Column(name = "fund_id", nullable = false)
    private Long fundId;

    @NotNull(message = "Units are required")
    @DecimalMin(value = "0.0", message = "Units must be non-negative")
    @Column(nullable = false, precision = 15, scale = 4)
    @Builder.Default
    private BigDecimal units = BigDecimal.ZERO;

    @NotNull(message = "Total value is required")
    @DecimalMin(value = "0.0", message = "Total value must be non-negative")
    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalValue = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id", insertable = false, updatable = false)
    private MutualFund mutualFund;
}
