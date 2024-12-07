package ru.mtuci.antivirus.entities.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseUpdateRequest {

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @NotBlank(message = "Код лицензии не может быть пустым")
    private String licenseCode;

    @NotBlank(message = "MAC не может быть пустым")
    private String macAddress;
}
