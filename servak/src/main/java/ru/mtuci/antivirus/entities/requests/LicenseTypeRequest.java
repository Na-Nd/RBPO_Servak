package ru.mtuci.antivirus.entities.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LicenseTypeRequest {

    @NotBlank(message = "Название типа лицензии не может быть пустым")
    private String name;

    @NotNull
    private int defaultDuration;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}