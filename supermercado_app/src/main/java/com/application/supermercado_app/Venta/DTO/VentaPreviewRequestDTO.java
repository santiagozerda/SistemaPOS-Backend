package com.application.supermercado_app.Venta.DTO;

import java.util.List;
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
public class VentaPreviewRequestDTO {

    private List<DetalleVentaRequestDTO> listDetalle;
}
