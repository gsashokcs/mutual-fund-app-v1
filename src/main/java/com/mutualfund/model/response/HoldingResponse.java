package com.mutualfund.model.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
