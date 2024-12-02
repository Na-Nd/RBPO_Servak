package ru.mtuci.antivirus.entities.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseTypeRequest {

    @NotBlank
    private String name;

    @NotNull
    private int defaultDuration;

    private String description;


}