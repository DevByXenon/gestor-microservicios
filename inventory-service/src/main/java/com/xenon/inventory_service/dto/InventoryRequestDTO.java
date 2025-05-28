
package com.xenon.inventory_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventoryRequestDTO {
    @NotNull
    private Long productId;
    
    @NotNull
    @PositiveOrZero
    private Integer quantity;
}
