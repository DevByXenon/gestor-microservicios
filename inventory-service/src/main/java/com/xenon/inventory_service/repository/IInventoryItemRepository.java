
package com.xenon.inventory_service.repository;

import com.xenon.inventory_service.model.InventoryItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByProductId(Long productId);
    List<InventoryItem> findByProductIdIn(List<Long> productIds); // Para buscar stock de varios productos
}
