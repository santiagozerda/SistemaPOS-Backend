package com.application.supermercado_app.Reportes.DTO;

import java.util.List;
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
public class ReporteVentaDTO<T> {

    private ReporteResumenDTO resumen;
    private List<T> tabla;
}
