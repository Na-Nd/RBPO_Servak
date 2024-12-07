package ru.mtuci.antivirus.entities.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseRequest {

    private Long productId;

    private Long userId;

    private Long licenseTypeId;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    private Integer deviceCount;

    private Integer duration;

}
