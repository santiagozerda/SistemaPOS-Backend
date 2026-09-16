package com.application.supermercado_app.Ticket.Repository;

import com.application.supermercado_app.Ticket.Model.Ticket;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    //Buscamos un tick por su numero
    @Query("""
        SELECT DISTINCT t
        FROM Ticket t
        LEFT JOIN FETCH t.venta v
        LEFT JOIN FETCH v.detalles d
        LEFT JOIN FETCH d.productos
        WHERE t.numeroTicket = :numeroTicket
    """)
    Optional<Ticket> findByNumeroTicketConDetalle(@Param("numeroTicket") String numeroTicket);

    //Busqueda de tick por venta realizada
    @Query("""
        SELECT DISTINCT t
        FROM Ticket t
        LEFT JOIN FETCH t.venta v
        LEFT JOIN FETCH v.detalles d
        LEFT JOIN FETCH d.productos
        WHERE v.idVenta = :idVenta
    """)
    Optional<Ticket> findByVentaIdVentaConDetalle(@Param("idVenta") Long idVenta);
}
