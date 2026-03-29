package com.portal.dynamics.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class D365AccountDto {
    private String d365Id;
    private String name;
    private String industry;
    private String country;
    private String portalCustomerId;
    private boolean synced;
    private String syncError;
}