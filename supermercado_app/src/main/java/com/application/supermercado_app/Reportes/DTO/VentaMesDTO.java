
package com.application.supermercado_app.Reportes.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class VentaMesDTO {
    
    private int semana;
    private Long cantidadVentas;
    private Double total;
}
