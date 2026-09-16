
package com.application.supermercado_app.Venta.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class VentaPreviewDTO {
    
    private List<DetalleVentaDTO> listaDetalle;
    private Double total;
}
