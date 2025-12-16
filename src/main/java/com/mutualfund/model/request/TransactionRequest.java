package com.mutualfund.model.request;

import jakarta.validation.constraints.DecimalMin;
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
public class TransactionRequest {

    @NotNull(message = "Fund ID is required")
    private Long fundId;

    @NotNull(message = "Units are required")
    @DecimalMin(value = "0.01", message = "Units must be greater than 0")
    private BigDecimal units;
}
