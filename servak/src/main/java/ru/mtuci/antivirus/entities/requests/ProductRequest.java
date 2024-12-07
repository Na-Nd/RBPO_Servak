package ru.mtuci.antivirus.entities.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProductRequest {

    @NotBlank(message = "Название продукта не может быть пустым")
    private String name;

    private boolean isBlocked;
}