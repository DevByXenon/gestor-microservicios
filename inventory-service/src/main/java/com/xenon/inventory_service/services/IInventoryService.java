
package com.xenon.inventory_service.services;

import com.xenon.inventory_service.dto.InventoryRequestDTO;
import com.xenon.inventory_service.dto.InventoryResponseDTO;
import com.xenon.inventory_service.dto.StockCheckRequestDTO;
import java.util.List;

public interface IInventoryService {
    InventoryResponseDTO getStockByProductId(Long productId);
    List<InventoryResponseDTO> getStockForProductIds(List<Long> productIds);
    void updateStock(InventoryRequestDTO inventoryRequestDTO); // Podría ser para sumar o restar
    void deductStock(Long productId, int quantityToDeduct);
    void addStock(Long productId, int quantityToAdd);
    boolean isInStock(Long productId, int quantityRequired);
    List<InventoryResponseDTO> checkMultipleStocks(List<StockCheckRequestDTO> checkRequests); // Para verificar varios items

}
