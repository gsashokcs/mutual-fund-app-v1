package com.mutualfund.model.request;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MutualFundRequest {

    @NotBlank(message = "Fund name is required")
    private String name;
}
