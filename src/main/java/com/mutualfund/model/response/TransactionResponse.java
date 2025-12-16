package com.mutualfund.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long transactionId;
    private Long userId;
    private Long fundId;
    private String fundName;
    private BigDecimal units;
    private BigDecimal nav;
    private String type;
    private LocalDateTime transactionDate;
}
