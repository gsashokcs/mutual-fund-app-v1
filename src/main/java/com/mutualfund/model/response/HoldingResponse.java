package com.mutualfund.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoldingResponse {
    private Long fundId;
    private String fundName;
    private BigDecimal units;
    private BigDecimal currentNav;
    private BigDecimal totalValue;
}
