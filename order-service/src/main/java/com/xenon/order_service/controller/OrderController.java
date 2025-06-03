
package com.xenon.order_service.controller;

import com.xenon.order_service.dto.OrderRequestDTO;
import com.xenon.order_service.dto.OrderResponseDTO;
import com.xenon.order_service.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO createdOrder = orderService.placeOrder(orderRequestDTO);
        // Devolvemos 201 Created, un código de estado más apropiado para la creación de recursos.
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.findAllOrders();
        return ResponseEntity.ok(orders); // Devuelve 200 OK con la lista
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable("id") Long orderId) {
        OrderResponseDTO order = orderService.findOrderById(orderId);
        return ResponseEntity.ok(order); // Devuelve 200 OK con el pedido
    }
}