
package com.application.supermercado_app.Venta.DTO;

import com.application.supermercado_app.Ticket.DTO.TicketReferenciaDTO;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
import java.time.LocalDate;
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
public class VentaCompletaDTO {

    private Long id;
    private LocalDate fecha;
    private Long idSucursal;
    private EstadoVenta estadoVenta;
    private List<DetalleVentaDTO> listaDetalle;
    private Double total;
    private Double totalDescuento;
    private TicketReferenciaDTO ticket;

}
