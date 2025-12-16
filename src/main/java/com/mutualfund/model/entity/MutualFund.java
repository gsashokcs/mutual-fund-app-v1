package com.mutualfund.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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
public class MutualFund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fundId;

    @NotBlank(message = "Fund name is required")
    @Column(nullable = false, unique = true)
    private String name;
}
