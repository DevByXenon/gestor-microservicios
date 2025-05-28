
package com.xenon.inventory_service.controller;

import com.xenon.inventory_service.dto.InventoryRequestDTO;
import com.xenon.inventory_service.dto.InventoryResponseDTO;
import com.xenon.inventory_service.dto.StockCheckRequestDTO;
import com.xenon.inventory_service.services.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    
    @Autowired
    private final InventoryService inventoryService;

    // Endpoint para que OTROS servicios (o Postman) verifiquen el stock
    @GetMapping("/check")
    public ResponseEntity<InventoryResponseDTO> checkStock(@RequestParam Long productId, @RequestParam int quantityRequired) {
        boolean inStock = inventoryService.isInStock(productId, quantityRequired);
        InventoryResponseDTO response = inventoryService.getStockByProductId(productId); // Obtener stock actual
        response.setInStock(inStock); // Actualizar el campo isInStock basado en la cantidad requerida
        return ResponseEntity.ok(response);
    }

    // Endpoint para verificar el stock de múltiples productos a la vez
    // Útil para el servicio de pedidos al validar un carrito completo
    @PostMapping("/check-multiple")
    public ResponseEntity<List<InventoryResponseDTO>> checkMultipleStocks(@RequestBody List<StockCheckRequestDTO> checkRequests) {
        List<InventoryResponseDTO> responses = inventoryService.checkMultipleStocks(checkRequests);
        return ResponseEntity.ok(responses);
    }

    // Endpoint para obtener el stock de un producto específico
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> getStock(@PathVariable Long productId) {
        InventoryResponseDTO response = inventoryService.getStockByProductId(productId);
        return ResponseEntity.ok(response);
    }

    // Endpoint para actualizar (establecer) el stock de un producto. Útil para carga inicial o ajustes manuales.
    @PostMapping("/update")
    public ResponseEntity<Void> updateStock(@Valid @RequestBody InventoryRequestDTO inventoryRequestDTO) {
        inventoryService.updateStock(inventoryRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Endpoint para deducir stock (usado por order-service)
    @PostMapping("/deduct")
    public ResponseEntity<Void> deductStock(@RequestParam Long productId, @RequestParam int quantity) {
        try {
            inventoryService.deductStock(productId, quantity);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Podrías tener un manejo de excepciones más sofisticado aquí
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un DTO de error
        }
    }
     // Endpoint para añadir stock (usado para devoluciones o reposiciones)
    @PostMapping("/add")
    public ResponseEntity<Void> addStock(@RequestParam Long productId, @RequestParam int quantity) {
        inventoryService.addStock(productId, quantity);
        return ResponseEntity.ok().build();
    }
    
}
