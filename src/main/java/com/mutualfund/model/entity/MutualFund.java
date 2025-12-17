package com.mutualfund.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mutual_funds", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
