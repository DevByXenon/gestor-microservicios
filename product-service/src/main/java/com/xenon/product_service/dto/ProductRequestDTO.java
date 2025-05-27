
package com.xenon.product_service.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    private String description;
    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser positivo")
    private Double price;
    @NotNull(message = "El stock no puede ser nulo")
    @PositiveOrZero(message = "El stock debe ser positivo o cero")
    private Integer stock;
}