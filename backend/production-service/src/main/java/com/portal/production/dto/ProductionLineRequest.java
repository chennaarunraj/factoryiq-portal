package com.portal.production.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionLineRequest {

    @NotNull(message = "Site ID is required")
    private UUID siteId;

    @NotBlank(message = "Code is required")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    private Integer capacityPerShift;
}