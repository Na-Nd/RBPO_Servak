package ru.mtuci.antivirus.entities.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivationRequest {

    @NotBlank(message = "Activation code cannot be empty")
    private String activationCode;

    @NotBlank(message = "Device name cannot be empty")
    private String deviceName;

    @NotBlank(message = "Mac address cannot be empty")
    private String macAddress;
}