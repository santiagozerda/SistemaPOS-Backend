
package com.application.supermercado_app.Reportes.DTO;

import com.application.supermercado_app.Pago.Model.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VentaDiaDTO {
    
    private String numeroTicket;
    private MetodoPago metodoPago;
    private Double total;
}
