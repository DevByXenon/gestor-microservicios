
package com.xenon.order_service.services;

import com.xenon.order_service.client.InventoryServiceFeignClient;
import com.xenon.order_service.client.ProductServiceFeignClient;
import com.xenon.order_service.client.dto.InventoryResponse;
import com.xenon.order_service.client.dto.ProductResponse;
import com.xenon.order_service.client.dto.StockCheckRequest;
import com.xenon.order_service.dto.OrderItemResponseDTO;
import com.xenon.order_service.dto.OrderRequestDTO;
import com.xenon.order_service.dto.OrderResponseDTO;
import com.xenon.order_service.model.Order;
import com.xenon.order_service.model.OrderItem;
import com.xenon.order_service.model.OrderStatus;
import com.xenon.order_service.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final ProductServiceFeignClient productClient;
    private final InventoryServiceFeignClient inventoryClient;

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO) {
        log.info("Iniciando proceso para crear pedido para el cliente: {}", orderRequestDTO.getCustomerName());

        // 1. VERIFICAR STOCK
        List<StockCheckRequest> stockRequests = orderRequestDTO.getItems().stream()
                .map(item -> new StockCheckRequest(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        List<InventoryResponse> stockResponses = inventoryClient.checkMultipleStocks(stockRequests);
        boolean allInStock = stockResponses.stream().allMatch(InventoryResponse::isInStock);

        if (!allInStock) {
            log.warn("Fallo en la creación del pedido: stock insuficiente.");
            throw new RuntimeException("Stock insuficiente para uno o más productos. Pedido cancelado.");
        }
        log.info("Verificación de stock exitosa.");

        // 2. CONSTRUIR LA ORDEN
        List<OrderItem> orderItems = orderRequestDTO.getItems().stream()
                .map(this::mapRequestItemToOrderItem)
                .collect(Collectors.toList());

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .customerName(orderRequestDTO.getCustomerName())
                .orderItems(orderItems)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        order.calculateTotalPrice();

        // 3. GUARDAR Y DEDUCIR STOCK
        Order savedOrder = orderRepository.save(order);
        log.info("Orden guardada con ID: {}. Procediendo a deducir stock.", savedOrder.getId());
        
        try {
            orderItems.forEach(item -> inventoryClient.deductStock(item.getProductId(), item.getQuantity()));
            savedOrder.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(savedOrder);
            log.info("Stock deducido. Estado de la orden {} actualizado a PROCESSING.", savedOrder.getId());
        } catch (Exception e) {
            log.error("CRÍTICO: La orden {} se creó pero falló la deducción de stock.", savedOrder.getId(), e);
            throw new RuntimeException("El pedido se creó pero no se pudo procesar el inventario. Contacte a soporte.");
        }

        return mapToOrderResponseDTO(savedOrder);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO findOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + orderId)); // Idealmente una excepción custom
        return mapToOrderResponseDTO(order);
    }

    // --- MÉTODOS PRIVADOS DE AYUDA (HELPERS) ---

    private OrderItem mapRequestItemToOrderItem(com.xenon.order_service.dto.OrderItemRequestDTO itemDto) {
        ProductResponse product = productClient.getProductById(itemDto.getProductId());
        return OrderItem.builder()
                .productId(itemDto.getProductId())
                .quantity(itemDto.getQuantity())
                .productName(product.getName())
                .pricePerUnit(BigDecimal.valueOf(product.getPrice()))
                .build();
    }

    private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponseDTO)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .status(order.getStatus().toString())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(itemDTOs)
                .build();
    }

    private OrderItemResponseDTO mapToOrderItemResponseDTO(OrderItem orderItem) {
        return OrderItemResponseDTO.builder()
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .pricePerUnit(orderItem.getPricePerUnit())
                .subTotal(orderItem.getPricePerUnit().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .build();
    }
}