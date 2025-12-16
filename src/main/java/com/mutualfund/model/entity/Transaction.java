package com.mutualfund.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Fund ID is required")
    @Column(name = "fund_id", nullable = false)
    private Long fundId;

    @NotNull(message = "Units are required")
    @DecimalMin(value = "0.01", message = "Units must be greater than 0")
    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal units;

    @NotNull(message = "NAV is required")
    @DecimalMin(value = "0.01", message = "NAV must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nav;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }

    public enum TransactionType {
        BUY,
        REDEEM
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id", insertable = false, updatable = false)
    private MutualFund mutualFund;
}
