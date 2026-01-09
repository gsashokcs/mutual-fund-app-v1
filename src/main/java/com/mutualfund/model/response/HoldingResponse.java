package com.mutualfund.model.response;

import java.math.BigDecimal;
import java.util.Objects;

public class HoldingResponse {
    private Long fundId;
    private String fundName;
    private BigDecimal units;
    private BigDecimal currentNav;
    private BigDecimal totalValue;

    public HoldingResponse() {
    }

    public HoldingResponse(Long fundId, String fundName, BigDecimal units, BigDecimal currentNav, BigDecimal totalValue) {
        this.fundId = fundId;
        this.fundName = fundName;
        this.units = units;
        this.currentNav = currentNav;
        this.totalValue = totalValue;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public BigDecimal getUnits() {
        return units;
    }

    public void setUnits(BigDecimal units) {
        this.units = units;
    }

    public BigDecimal getCurrentNav() {
        return currentNav;
    }

    public void setCurrentNav(BigDecimal currentNav) {
        this.currentNav = currentNav;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoldingResponse that = (HoldingResponse) o;
        return Objects.equals(fundId, that.fundId) && Objects.equals(fundName, that.fundName) && Objects.equals(units, that.units) && Objects.equals(currentNav, that.currentNav) && Objects.equals(totalValue, that.totalValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fundId, fundName, units, currentNav, totalValue);
    }

    @Override
    public String toString() {
        return "HoldingResponse{" + "fundId=" + fundId + ", fundName='" + fundName + '\'' + ", units=" + units + ", currentNav=" + currentNav + ", totalValue=" + totalValue + '}';
    }

    public static HoldingResponseBuilder builder() {
        return new HoldingResponseBuilder();
    }

    public static class HoldingResponseBuilder {
        private Long fundId;
        private String fundName;
        private BigDecimal units;
        private BigDecimal currentNav;
        private BigDecimal totalValue;

        HoldingResponseBuilder() {
        }

        public HoldingResponseBuilder fundId(Long fundId) {
            this.fundId = fundId;
            return this;
        }

        public HoldingResponseBuilder fundName(String fundName) {
            this.fundName = fundName;
            return this;
        }

        public HoldingResponseBuilder units(BigDecimal units) {
            this.units = units;
            return this;
        }

        public HoldingResponseBuilder currentNav(BigDecimal currentNav) {
            this.currentNav = currentNav;
            return this;
        }

        public HoldingResponseBuilder totalValue(BigDecimal totalValue) {
            this.totalValue = totalValue;
            return this;
        }

        public HoldingResponse build() {
            return new HoldingResponse(fundId, fundName, units, currentNav, totalValue);
        }
    }
}
