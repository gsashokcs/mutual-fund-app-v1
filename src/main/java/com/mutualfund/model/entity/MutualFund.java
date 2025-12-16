package com.mutualfund.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "mutual_funds",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "nav_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MutualFund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fundId;

    @NotBlank(message = "Fund name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "NAV is required")
    @DecimalMin(value = "0.01", message = "NAV must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nav;

    @NotNull(message = "NAV date is required")
    @Column(name = "nav_date", nullable = false)
    private LocalDate navDate;
}
