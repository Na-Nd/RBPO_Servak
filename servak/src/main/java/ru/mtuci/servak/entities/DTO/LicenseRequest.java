package ru.mtuci.servak.entities.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseRequest {
    private Long productId;
    private Long ownerId;
    private Long licenseTypeId;
    private String code;
    private String description;
    private Integer deviceCount;
    private Integer duration;
}
