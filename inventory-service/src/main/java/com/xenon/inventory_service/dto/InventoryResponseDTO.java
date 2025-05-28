
package com.xenon.inventory_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponseDTO {
    private Long productId;
    private Integer quantity;
    private boolean isInStock;
}
