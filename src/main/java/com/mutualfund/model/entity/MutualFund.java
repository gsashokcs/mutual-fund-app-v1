package com.mutualfund.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Entity
@Table(name = "mutual_funds", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@Schema(description = "Mutual fund metadata entity (NAV values stored separately in Nav table)")
public class MutualFund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the mutual fund", example = "1")
    private Long fundId;

    @NotBlank(message = "Fund name is required")
    @Column(nullable = false, unique = true)
    @Schema(description = "Name of the mutual fund", example = "HDFC Equity Fund")
    private String name;

    public MutualFund() {
    }

    public MutualFund(Long fundId, String name) {
        this.fundId = fundId;
        this.name = name;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutualFund that = (MutualFund) o;
        return Objects.equals(fundId, that.fundId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fundId, name);
    }

    @Override
    public String toString() {
        return "MutualFund{" + "fundId=" + fundId + ", name='" + name + '\'' + '}';
    }

    public static MutualFundBuilder builder() {
        return new MutualFundBuilder();
    }

    public static class MutualFundBuilder {
        private Long fundId;
        private String name;

        MutualFundBuilder() {
        }

        public MutualFundBuilder fundId(Long fundId) {
            this.fundId = fundId;
            return this;
        }

        public MutualFundBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MutualFund build() {
            return new MutualFund(fundId, name);
        }
    }
}
