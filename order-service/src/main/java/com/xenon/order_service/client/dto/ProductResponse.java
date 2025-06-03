
package com.xenon.order_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Un DTO simple que representa la estructura del JSON que esperamos
// recibir desde product-service. Los nombres de los campos DEBEN coincidir.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
}