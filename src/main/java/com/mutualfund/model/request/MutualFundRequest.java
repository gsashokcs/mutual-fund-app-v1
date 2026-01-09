package com.mutualfund.model.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Request to create a new mutual fund (metadata only, NAV managed separately)")
public class MutualFundRequest {

    @NotBlank(message = "Fund name is required")
    @Schema(description = "Name of the mutual fund", example = "Growth Fund Alpha", required = true)
    private String name;

    public MutualFundRequest() {
    }

    public MutualFundRequest(String name) {
        this.name = name;
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
        MutualFundRequest that = (MutualFundRequest) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "MutualFundRequest{" + "name='" + name + '\'' + '}';
    }

    public static MutualFundRequestBuilder builder() {
        return new MutualFundRequestBuilder();
    }

    public static class MutualFundRequestBuilder {
        private String name;

        MutualFundRequestBuilder() {
        }

        public MutualFundRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MutualFundRequest build() {
            return new MutualFundRequest(name);
        }
    }
}
