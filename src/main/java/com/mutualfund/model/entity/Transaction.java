package com.mutualfund.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "transactions")
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
        BUY, REDEEM
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id", insertable = false, updatable = false)
    private MutualFund mutualFund;

    public Transaction() {
    }

    public Transaction(Long transactionId, Long userId, Long fundId, BigDecimal units, BigDecimal nav, TransactionType type, LocalDateTime transactionDate, User user, MutualFund mutualFund) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.fundId = fundId;
        this.units = units;
        this.nav = nav;
        this.type = type;
        this.transactionDate = transactionDate;
        this.user = user;
        this.mutualFund = mutualFund;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public BigDecimal getUnits() {
        return units;
    }

    public void setUnits(BigDecimal units) {
        this.units = units;
    }

    public BigDecimal getNav() {
        return nav;
    }

    public void setNav(BigDecimal nav) {
        this.nav = nav;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MutualFund getMutualFund() {
        return mutualFund;
    }

    public void setMutualFund(MutualFund mutualFund) {
        this.mutualFund = mutualFund;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(userId, that.userId) && Objects.equals(fundId, that.fundId) && Objects.equals(units, that.units) && Objects.equals(nav, that.nav) && type == that.type && Objects.equals(transactionDate, that.transactionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, userId, fundId, units, nav, type, transactionDate);
    }

    @Override
    public String toString() {
        return "Transaction{" + "transactionId=" + transactionId + ", userId=" + userId + ", fundId=" + fundId + ", units=" + units + ", nav=" + nav + ", type=" + type + ", transactionDate=" + transactionDate + '}';
    }

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public static class TransactionBuilder {
        private Long transactionId;
        private Long userId;
        private Long fundId;
        private BigDecimal units;
        private BigDecimal nav;
        private TransactionType type;
        private LocalDateTime transactionDate;
        private User user;
        private MutualFund mutualFund;

        TransactionBuilder() {
        }

        public TransactionBuilder transactionId(Long transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public TransactionBuilder fundId(Long fundId) {
            this.fundId = fundId;
            return this;
        }

        public TransactionBuilder units(BigDecimal units) {
            this.units = units;
            return this;
        }

        public TransactionBuilder nav(BigDecimal nav) {
            this.nav = nav;
            return this;
        }

        public TransactionBuilder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public TransactionBuilder transactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public TransactionBuilder user(User user) {
            this.user = user;
            return this;
        }

        public TransactionBuilder mutualFund(MutualFund mutualFund) {
            this.mutualFund = mutualFund;
            return this;
        }

        public Transaction build() {
            return new Transaction(transactionId, userId, fundId, units, nav, type, transactionDate, user, mutualFund);
        }
    }
}
