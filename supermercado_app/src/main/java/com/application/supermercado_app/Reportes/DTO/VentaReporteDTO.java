
package com.application.supermercado_app.Reportes.DTO;

import com.application.supermercado_app.Pago.Model.MetodoPago;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class VentaReporteDTO {
    
    private String numeroTicket;
    private LocalDateTime fechaVenta;
    private MetodoPago metodoPago;
    private Double total;
}
