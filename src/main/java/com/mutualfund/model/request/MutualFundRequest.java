package com.mutualfund.model.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new mutual fund (metadata only, NAV managed separately)")
public class MutualFundRequest {

    @NotBlank(message = "Fund name is required")
    @Schema(description = "Name of the mutual fund", example = "Growth Fund Alpha", required = true)
    private String name;
}
