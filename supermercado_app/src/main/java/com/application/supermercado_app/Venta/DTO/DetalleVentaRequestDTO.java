
package com.application.supermercado_app.Venta.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class DetalleVentaRequestDTO {
    
    private Long idProducto;
    private Integer cantidadVendida;
}
