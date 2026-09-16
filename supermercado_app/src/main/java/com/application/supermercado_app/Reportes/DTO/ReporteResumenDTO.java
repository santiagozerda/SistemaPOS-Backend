package com.application.supermercado_app.Reportes.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReporteResumenDTO {

    private Double totalVendido;
    private Long cantidadVentas;
    private Double promedioVenta;

}
