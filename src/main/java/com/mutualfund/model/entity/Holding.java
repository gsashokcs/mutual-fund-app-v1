package com.mutualfund.model.entity;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "holdings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "fund_id"}))
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
    private BigDecimal units = BigDecimal.ZERO;

    @NotNull(message = "Total value is required")
    @DecimalMin(value = "0.0", message = "Total value must be non-negative")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalValue = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id", insertable = false, updatable = false)
    private MutualFund mutualFund;

    public Holding() {
        this.units = BigDecimal.ZERO;
        this.totalValue = BigDecimal.ZERO;
    }

    public Holding(Long id, Long userId, Long fundId, BigDecimal units, BigDecimal totalValue, User user, MutualFund mutualFund) {
        this.id = id;
        this.userId = userId;
        this.fundId = fundId;
        this.units = units;
        this.totalValue = totalValue;
        this.user = user;
        this.mutualFund = mutualFund;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
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
        Holding holding = (Holding) o;
        return Objects.equals(id, holding.id) && Objects.equals(userId, holding.userId) && Objects.equals(fundId, holding.fundId) && Objects.equals(units, holding.units) && Objects.equals(totalValue, holding.totalValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, fundId, units, totalValue);
    }

    @Override
    public String toString() {
        return "Holding{" + "id=" + id + ", userId=" + userId + ", fundId=" + fundId + ", units=" + units + ", totalValue=" + totalValue + '}';
    }

    public static HoldingBuilder builder() {
        return new HoldingBuilder();
    }

    public static class HoldingBuilder {
        private Long id;
        private Long userId;
        private Long fundId;
        private BigDecimal units = BigDecimal.ZERO;
        private BigDecimal totalValue = BigDecimal.ZERO;
        private User user;
        private MutualFund mutualFund;

        HoldingBuilder() {
        }

        public HoldingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public HoldingBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public HoldingBuilder fundId(Long fundId) {
            this.fundId = fundId;
            return this;
        }

        public HoldingBuilder units(BigDecimal units) {
            this.units = units;
            return this;
        }

        public HoldingBuilder totalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
            return this;
        }

        public HoldingBuilder user(User user) {
            this.user = user;
            return this;
        }

        public HoldingBuilder mutualFund(MutualFund mutualFund) {
            this.mutualFund = mutualFund;
            return this;
        }

        public Holding build() {
            return new Holding(id, userId, fundId, units, totalValue, user, mutualFund);
        }
    }
}
