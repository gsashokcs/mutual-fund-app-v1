package com.mutualfund.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update or create NAV for a mutual fund on a specific date")
public class NavUpdateRequest {

    @NotNull(message = "NAV is required")
    @DecimalMin(value = "0.01", message = "NAV must be greater than 0")
    @Schema(description = "Net Asset Value per unit", example = "155.50", required = true)
    private BigDecimal nav;

    @NotNull(message = "NAV date is required")
    @Schema(description = "Date of the NAV value (YYYY-MM-DD)", example = "2025-12-16", required = true)
    private LocalDate navDate;

    public NavUpdateRequest() {
    }

    public NavUpdateRequest(BigDecimal nav, LocalDate navDate) {
        this.nav = nav;
        this.navDate = navDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NavUpdateRequest that = (NavUpdateRequest) o;
        return Objects.equals(nav, that.nav) && Objects.equals(navDate, that.navDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nav, navDate);
    }

    @Override
    public String toString() {
        return "NavUpdateRequest{" + "nav=" + nav + ", navDate=" + navDate + '}';
    }

    public static NavUpdateRequestBuilder builder() {
        return new NavUpdateRequestBuilder();
    }

    public static class NavUpdateRequestBuilder {
        private BigDecimal nav;
        private LocalDate navDate;

        NavUpdateRequestBuilder() {
        }

        public NavUpdateRequestBuilder nav(BigDecimal nav) {
            this.nav = nav;
            return this;
        }

        public NavUpdateRequestBuilder navDate(LocalDate navDate) {
            this.navDate = navDate;
            return this;
        }

        public NavUpdateRequest build() {
            return new NavUpdateRequest(nav, navDate);
        }
    }
}
