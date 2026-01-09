package com.mutualfund.model.request;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TransactionRequest {

    @NotNull(message = "Fund ID is required")
    private Long fundId;

    @NotNull(message = "Units are required")
    @DecimalMin(value = "0.01", message = "Units must be greater than 0")
    private BigDecimal units;

    public TransactionRequest() {
    }

    public TransactionRequest(Long fundId, BigDecimal units) {
        this.fundId = fundId;
        this.units = units;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionRequest that = (TransactionRequest) o;
        return Objects.equals(fundId, that.fundId) && Objects.equals(units, that.units);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fundId, units);
    }

    @Override
    public String toString() {
        return "TransactionRequest{" + "fundId=" + fundId + ", units=" + units + '}';
    }

    public static TransactionRequestBuilder builder() {
        return new TransactionRequestBuilder();
    }

    public static class TransactionRequestBuilder {
        private Long fundId;
        private BigDecimal units;

        TransactionRequestBuilder() {
        }

        public TransactionRequestBuilder fundId(Long fundId) {
            this.fundId = fundId;
            return this;
        }

        public TransactionRequestBuilder units(BigDecimal units) {
            this.units = units;
            return this;
        }

        public TransactionRequest build() {
            return new TransactionRequest(fundId, units);
        }
    }
}
