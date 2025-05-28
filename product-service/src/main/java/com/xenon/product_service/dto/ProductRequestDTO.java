
package com.xenon.product_service.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    private String description;
    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser positivo")
    private Double price;
}