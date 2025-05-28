
package com.xenon.inventory_service.services;

import com.xenon.inventory_service.dto.InventoryRequestDTO;
import com.xenon.inventory_service.dto.InventoryResponseDTO;
import com.xenon.inventory_service.dto.StockCheckRequestDTO;
import com.xenon.inventory_service.model.InventoryItem;
import com.xenon.inventory_service.repository.IInventoryItemRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService implements IInventoryService {
    
    @Autowired
    private IInventoryItemRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDTO getStockByProductId(Long productId) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
            .orElseGet(() -> InventoryItem.builder().productId(productId).quantity(0).build()); // Devuelve 0 si no existe
        return mapToResponseDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getStockForProductIds(List<Long> productIds) {
        return inventoryRepository.findByProductIdIn(productIds).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStock(InventoryRequestDTO requestDTO) { // Usado para establecer una cantidad específica
        InventoryItem item = inventoryRepository.findByProductId(requestDTO.getProductId())
            .orElseGet(() -> {
                InventoryItem newItem = new InventoryItem();
                newItem.setProductId(requestDTO.getProductId());
                return newItem;
            });
        item.setQuantity(requestDTO.getQuantity());
        inventoryRepository.save(item);
    }

    @Override
    @Transactional
    public void deductStock(Long productId, int quantityToDeduct) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado en inventario: " + productId));
        if (item.getQuantity() < quantityToDeduct) {
            throw new RuntimeException("Stock insuficiente para producto: " + productId);
        }
        item.setQuantity(item.getQuantity() - quantityToDeduct);
        inventoryRepository.save(item);
    }

    @Override
    @Transactional
    public void addStock(Long productId, int quantityToAdd) {
         InventoryItem item = inventoryRepository.findByProductId(productId)
            .orElseGet(() -> InventoryItem.builder().productId(productId).quantity(0).build());
        item.setQuantity(item.getQuantity() + quantityToAdd);
        inventoryRepository.save(item);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(Long productId, int quantityRequired) {
        Optional<InventoryItem> itemOpt = inventoryRepository.findByProductId(productId);
        return itemOpt.map(item -> item.getQuantity() >= quantityRequired).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> checkMultipleStocks(List<StockCheckRequestDTO> checkRequests) {
        List<Long> productIds = checkRequests.stream().map(StockCheckRequestDTO::getProductId).collect(Collectors.toList());
        List<InventoryItem> itemsInDb = inventoryRepository.findByProductIdIn(productIds);

        return checkRequests.stream().map(req -> {
            Optional<InventoryItem> foundItem = itemsInDb.stream()
                    .filter(dbItem -> dbItem.getProductId().equals(req.getProductId()))
                    .findFirst();
            boolean hasStock = foundItem.map(item -> item.getQuantity() >= req.getQuantityRequired()).orElse(false);
            int currentStock = foundItem.map(InventoryItem::getQuantity).orElse(0);
            return InventoryResponseDTO.builder()
                    .productId(req.getProductId())
                    .quantity(currentStock)
                    .isInStock(hasStock)
                    .build();
        }).collect(Collectors.toList());
    }


    private InventoryResponseDTO mapToResponseDTO(InventoryItem item) {
        return InventoryResponseDTO.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .isInStock(item.getQuantity() > 0)
                .build();
    }
}
