
package com.xenon.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    private String customerName;

    @NotEmpty(message = "La lista de ítems no puede estar vacía")
    @Valid // importante!!!!! Valida cada objeto dentro de la lista
    private List<OrderItemRequestDTO> items;
}