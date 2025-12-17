package com.mutualfund.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entity representing the Net Asset Value (NAV) for a mutual fund on a specific date. Tracks historical NAV values for each fund with soft delete support.
 *
 * @author Mutual Fund Management System
 * @version 1.0
 */
@Entity
@Schema(description = "Historical NAV entry for a mutual fund on a specific date")
@Table(name = "nav_history", uniqueConstraints = @UniqueConstraint(columnNames = {"fund_id", "nav_date"}))
public class Nav {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the NAV entry", example = "1")
    private Long navId;

    @NotNull(message = "Fund ID is required")
    @Column(name = "fund_id", nullable = false)
    @Schema(description = "Foreign key to the mutual fund", example = "1")
    private Long fundId;

    @NotNull(message = "NAV is required")
    @DecimalMin(value = "0.01", message = "NAV must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Net Asset Value per unit", example = "155.50")
    private BigDecimal nav;

    @NotNull(message = "NAV date is required")
    @Column(name = "nav_date", nullable = false)
    @Schema(description = "Date of the NAV value", example = "2025-12-16")
    private LocalDate navDate;

    @Column(nullable = false)
    private Boolean deleted = false;

    public Nav() {
        this.deleted = false;
    }

    public Nav(Long navId, Long fundId, BigDecimal nav, LocalDate navDate, Boolean deleted) {
        this.navId = navId;
        this.fundId = fundId;
        this.nav = nav;
        this.navDate = navDate;
        this.deleted = deleted;
    }

    public Long getNavId() {
        return navId;
    }

    public void setNavId(Long navId) {
        this.navId = navId;
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

    public BigDecimal getNav() {
        return nav;
    }

    public void setNav(BigDecimal nav) {
        this.nav = nav;
    }

    public LocalDate getNavDate() {
        return navDate;
    }

    public void setNavDate(LocalDate navDate) {
        this.navDate = navDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nav nav1 = (Nav) o;
        return Objects.equals(navId, nav1.navId) && Objects.equals(fundId, nav1.fundId) && Objects.equals(nav, nav1.nav) && Objects.equals(navDate, nav1.navDate) && Objects.equals(deleted, nav1.deleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(navId, fundId, nav, navDate, deleted);
    }

    @Override
    public String toString() {
        return "Nav{" + "navId=" + navId + ", fundId=" + fundId + ", nav=" + nav + ", navDate=" + navDate + ", deleted=" + deleted + '}';
    }

    public static NavBuilder builder() {
        return new NavBuilder();
    }

    public static class NavBuilder {
        private Long navId;
        private Long fundId;
        private BigDecimal nav;
        private LocalDate navDate;
        private Boolean deleted = false;

        NavBuilder() {
        }

        public NavBuilder navId(Long navId) {
            this.navId = navId;
            return this;
        }

        public NavBuilder fundId(Long fundId) {
            this.fundId = fundId;
            return this;
        }

        public NavBuilder nav(BigDecimal nav) {
            this.nav = nav;
            return this;
        }

        public NavBuilder navDate(LocalDate navDate) {
            this.navDate = navDate;
            return this;
        }

        public NavBuilder deleted(Boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Nav build() {
            return new Nav(navId, fundId, nav, navDate, deleted);
        }
    }
}
