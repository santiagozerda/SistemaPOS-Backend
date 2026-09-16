
package com.application.supermercado_app.Pago.DTO;

import com.application.supermercado_app.Pago.Model.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class PagoRequestDTO {
    
    private Long ventaId;
    private MetodoPago metodoPago;
    private Double totalPagar;
    private Double montoEntregado;

    
}
