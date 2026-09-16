package com.application.supermercado_app.Ticket.DTO;

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
public class TicketReferenciaDTO {

    private Long idTicket;
    private String numeroTicket;
}
