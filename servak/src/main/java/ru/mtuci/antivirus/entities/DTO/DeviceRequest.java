package ru.mtuci.antivirus.entities.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRequest {

    @NotBlank(message = "Device name cannot be empty")
    private String deviceName;

    @NotBlank(message = "Mac address cannot be empty")
    private String macAddress;

    private Long userId;
}