package com.application.supermercado_app.Venta.DTO;

import com.application.supermercado_app.Pago.Model.EstadoPago;
import com.application.supermercado_app.Pago.Model.MetodoPago;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
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
public class VentaResumenDTO {

    private Long ventaId;
    private LocalDate fecha;
    private Long idSucursal;
    private EstadoVenta estadoVenta;
    private Double total;
    private Double totalDescuento;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;
}
