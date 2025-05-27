
package com.xenon.product_service.services;

import com.xenon.product_service.dto.ProductRequestDTO;
import com.xenon.product_service.dto.ProductResponseDTO;
import java.util.List;

public interface IProductService {
    
    ProductResponseDTO createProduct(ProductRequestDTO ProductorRequestDTO);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long id);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);
    void deleteProduct(Long id);
    // Más adelante: boolean checkStock(Long productId, int quantity);
    // Más adelante: void updateStock(Long productId, int quantityChange);
    
}
