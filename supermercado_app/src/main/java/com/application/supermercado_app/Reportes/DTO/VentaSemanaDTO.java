package com.application.supermercado_app.Reportes.DTO;

import java.time.LocalDate;
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
public class VentaSemanaDTO {

    private LocalDate dia;
    private Long cantidadVentas;
    private Double total;
}
