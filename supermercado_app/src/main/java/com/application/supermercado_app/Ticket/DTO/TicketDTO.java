package com.application.supermercado_app.Ticket.DTO;

import com.application.supermercado_app.Pago.Model.MetodoPago;
import com.application.supermercado_app.Sucursal.DTO.SucursalDTO;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
import java.time.LocalDateTime;
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
public class TicketDTO {

    private Long idTicket;
    private String numeroTicket;
    private LocalDateTime fecha;

    private SucursalDTO sucursal;

    private Double total;
    private MetodoPago metodoPago;

    private Double montoRecibido;
    private Double vuelto;
    private String nombreCliente;
    
    private EstadoVenta estadoVenta;
    private Long idVenta;
    private List<TicketDetalleDTO> listDetalle;

}
