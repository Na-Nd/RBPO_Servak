package ru.mtuci.servak.entities.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivationRequest {

    @NotBlank(message = "Поле activationCode не должно быть пустым.")
    private String activationCode;

    @NotBlank(message = "Поле deviceName не должно быть пустым.")
    private String deviceName;

    @NotBlank(message = "Поле macAddress не должно быть пустым.")
    private String macAddress;
}
