package com.mutualfund.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to update or create NAV for a mutual fund on a specific date")
public class NavUpdateRequest {

    @NotNull(message = "NAV is required")
    @DecimalMin(value = "0.01", message = "NAV must be greater than 0")
    @Schema(description = "Net Asset Value per unit", example = "155.50", required = true)
    private BigDecimal nav;

    @NotNull(message = "NAV date is required")
    @Schema(
            description = "Date of the NAV value (YYYY-MM-DD)",
            example = "2025-12-16",
            required = true)
    private LocalDate navDate;
}
