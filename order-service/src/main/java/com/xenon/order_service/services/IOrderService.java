
package com.xenon.order_service.services;

import com.xenon.order_service.dto.OrderRequestDTO;
import com.xenon.order_service.dto.OrderResponseDTO;
import java.util.List;

/**
 * Interfaz que define el contrato para las operaciones de negocio relacionadas con los pedidos.
 */
public interface IOrderService {

    /**
     * Procesa la creación de un nuevo pedido.
     *
     * @param orderRequestDTO Los datos del pedido a crear.
     * @return Un DTO con la información del pedido ya creado.
     */
    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO);

    /**
     * Busca y devuelve todos los pedidos existentes.
     *
     * @return Una lista de DTOs, cada uno representando un pedido.
     */
    List<OrderResponseDTO> findAllOrders();

    /**
     * Busca un pedido específico por su ID.
     *
     * @param orderId El ID del pedido a buscar.
     * @return Un DTO con la información del pedido encontrado.
     */
    OrderResponseDTO findOrderById(Long orderId);
}