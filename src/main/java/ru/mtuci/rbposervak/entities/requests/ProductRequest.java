package ru.mtuci.rbposervak.entities.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Название продукта не может быть пустым")
    private String name;

    private boolean isBlocked;
}