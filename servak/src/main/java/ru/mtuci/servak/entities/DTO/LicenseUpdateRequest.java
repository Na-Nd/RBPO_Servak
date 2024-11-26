package ru.mtuci.servak.entities.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseUpdateRequest {
    private String username;
    private String password;
    private String licenseKey;
}
