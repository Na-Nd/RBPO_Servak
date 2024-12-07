package ru.mtuci.antivirus.entities.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseInfoRequest {

    @NotBlank(message = "MAC не может быть пустым")
    private String macAddress;

    @NotBlank(message = "Код лиценщзии не может быть пустым")
    private String licenseCode;

}
