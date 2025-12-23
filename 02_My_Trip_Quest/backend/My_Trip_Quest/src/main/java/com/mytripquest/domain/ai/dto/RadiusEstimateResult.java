package com.mytripquest.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadiusEstimateResult {
    private String name;
    private Integer radius;
}
