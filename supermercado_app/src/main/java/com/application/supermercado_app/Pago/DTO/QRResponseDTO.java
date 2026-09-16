
package com.application.supermercado_app.Pago.DTO;

import com.application.supermercado_app.Pago.Model.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class QRResponseDTO {
    
    private Long ventaId;
    private Long pagoId;
    private EstadoPago estadoPago;
    private String initPoint;
    private String preferenceId;
    
}
