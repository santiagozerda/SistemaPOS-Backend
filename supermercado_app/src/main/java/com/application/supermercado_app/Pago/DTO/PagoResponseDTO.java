package com.application.supermercado_app.Pago.DTO;

import com.application.supermercado_app.Pago.Model.EstadoPago;
import com.application.supermercado_app.Pago.Model.MetodoPago;
import com.application.supermercado_app.Ticket.DTO.TicketReferenciaDTO;
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
public class PagoResponseDTO {

    private Long idPago;
    private Long idVenta;
    private EstadoPago estadoPago;
    private Double monto;
    private MetodoPago metodoPago;
    private LocalDate fecha;
    private TicketReferenciaDTO ticket;
}
