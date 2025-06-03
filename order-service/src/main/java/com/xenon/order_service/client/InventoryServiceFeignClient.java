
package com.xenon.order_service.client;

import com.xenon.order_service.client.dto.InventoryResponse;
import com.xenon.order_service.client.dto.StockCheckRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryServiceFeignClient {

    // Endpoint para verificar el stock de múltiples productos a la vez.
    // Recibe una lista en el cuerpo de la petición.
    @PostMapping("/api/inventory/check-multiple")
    List<InventoryResponse> checkMultipleStocks(@RequestBody List<StockCheckRequest> checkRequests);

    // Endpoint para deducir el stock de un producto.
    // Recibe los datos como parámetros de la petición.
    // El nombre en @RequestParam("nombre") debe coincidir con el del Controller de destino.
    @PostMapping("/api/inventory/deduct")
    ResponseEntity<Void> deductStock(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity);
}