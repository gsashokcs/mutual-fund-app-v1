package com.mutualfund.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionResponse {
    private Long transactionId;
    private Long userId;
    private Long fundId;
    private String fundName;
    private BigDecimal units;
    private BigDecimal nav;
    private String type;
    private LocalDateTime transactionDate;

    public TransactionResponse() {
    }

    public TransactionResponse(Long transactionId, Long userId, Long fundId, String fundName, BigDecimal units, BigDecimal nav, String type, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.fundId = fundId;
        this.fundName = fundName;
        this.units = units;
        this.nav = nav;
        this.type = type;
        this.transactionDate = transactionDate;
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

    public BigDecimal getNav() {
        return nav;
    }

    public void setNav(BigDecimal nav) {
        this.nav = nav;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionResponse that = (TransactionResponse) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(userId, that.userId) && Objects.equals(fundId, that.fundId) && Objects.equals(fundName, that.fundName) && Objects.equals(units, that.units) && Objects.equals(nav, that.nav) && Objects.equals(type, that.type) && Objects.equals(transactionDate, that.transactionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, userId, fundId, fundName, units, nav, type, transactionDate);
    }

    @Override
    public String toString() {
        return "TransactionResponse{" + "transactionId=" + transactionId + ", userId=" + userId + ", fundId=" + fundId + ", fundName='" + fundName + '\'' + ", units=" + units + ", nav=" + nav + ", type='" + type + '\'' + ", transactionDate=" + transactionDate + '}';
    }

    public static TransactionResponseBuilder builder() {
        return new TransactionResponseBuilder();
    }

    public static class TransactionResponseBuilder {
        private Long transactionId;
        private Long userId;
        private Long fundId;
        private String fundName;
        private BigDecimal units;
        private BigDecimal nav;
        private String type;
        private LocalDateTime transactionDate;

        TransactionResponseBuilder() {
        }

        public TransactionResponseBuilder transactionId(Long transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public TransactionResponseBuilder fundId(Long fundId) {
            this.fundId = fundId;
            return this;
        }

        public TransactionResponseBuilder fundName(String fundName) {
            this.fundName = fundName;
            return this;
        }

        public TransactionResponseBuilder units(BigDecimal units) {
            this.units = units;
            return this;
        }

        public TransactionResponseBuilder nav(BigDecimal nav) {
            this.nav = nav;
            return this;
        }

        public TransactionResponseBuilder type(String type) {
            this.type = type;
            return this;
        }

        public TransactionResponseBuilder transactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public TransactionResponse build() {
            return new TransactionResponse(transactionId, userId, fundId, fundName, units, nav, type, transactionDate);
        }
    }
}
