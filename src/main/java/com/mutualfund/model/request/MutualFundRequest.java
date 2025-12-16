package com.mutualfund.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MutualFundRequest {

    @NotBlank(message = "Fund name is required")
    private String name;

    @NotNull(message = "NAV is required")
    @DecimalMin(value = "0.01", message = "NAV must be greater than 0")
    private BigDecimal nav;
}
