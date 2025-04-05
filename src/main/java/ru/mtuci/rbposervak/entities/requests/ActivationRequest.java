package ru.mtuci.rbposervak.entities.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActivationRequest {

    @NotBlank(message = "Код активации не может быть пустым")
    private String activationCode;

    @NotBlank(message = "Имя устройства не может быть пустым")
    private String deviceName;

    @NotBlank(message = "MAC не может быть пустым")
    private String macAddress;
}