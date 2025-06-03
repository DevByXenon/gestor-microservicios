
package com.xenon.order_service.client;

import com.xenon.order_service.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @FeignClient(name = "NOMBRE_DEL_SERVICIO_EN_EUREKA")
// El nombre DEBE coincidir exactamente con el valor de 'spring.application.name'
// del microservicio al que quieres llamar.
@FeignClient(name = "product-service")
public interface ProductServiceFeignClient {

    // La firma de este método debe ser idéntica a la del endpoint en el Controller del otro servicio.
    // Esto incluye:
    // 1. La anotación de Mapeo HTTP (@GetMapping, @PostMapping, etc.)
    // 2. El path de la URL
    // 3. Los parámetros (@PathVariable, @RequestParam, @RequestBody)
    // 4. El tipo de dato de retorno (debe poder ser deserializado desde el JSON de respuesta)
    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);
}