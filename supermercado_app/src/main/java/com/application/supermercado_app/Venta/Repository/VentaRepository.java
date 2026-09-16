package com.application.supermercado_app.Venta.Repository;

import com.application.supermercado_app.Reportes.DTO.ReporteResumenDTO;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
import com.application.supermercado_app.Venta.Model.Venta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    //Historial de Ventas
    List<Venta> findByEstadoAndFechaVentaBetweenOrderByFechaVentaAsc(
            EstadoVenta estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    //Resumen General y muestra de detalles de metricas
    @Query("""
        SELECT new com.application.supermercado_app.Reportes.DTO.ReporteResumenDTO(
            COALESCE(SUM(v.total), 0.0),
            COUNT(v),
            COALESCE(AVG(v.total), 0.0)
        )
        FROM Venta v
        WHERE v.estado = :estado
        AND v.fechaVenta BETWEEN :inicio AND :fin
    """)
    ReporteResumenDTO obtenerResumenGeneral(
            @Param("estado") EstadoVenta estado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
        SELECT DISTINCT v
        FROM Venta v
        JOIN FETCH v.ticket
        JOIN FETCH v.pago
        WHERE v.estado = :estado
        AND v.fechaVenta BETWEEN :inicio AND :fin
        ORDER BY v.fechaVenta ASC
    """)
    List<Venta> findVentasParaReporte(
            @Param("estado") EstadoVenta estado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
        SELECT DISTINCT v
        FROM Venta v
        LEFT JOIN FETCH v.detalles d
        LEFT JOIN FETCH d.productos
        LEFT JOIN FETCH v.pago
        WHERE v.idVenta = :id
    """)
    Optional<Venta> findVentaConDetalleYPago(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT v
        FROM Venta v
        LEFT JOIN FETCH v.ticket t
        WHERE v.idVenta = :id
    """)
    Optional<Venta> findVentaConTicket(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT v
        FROM Venta v
        LEFT JOIN FETCH v.pago
        LEFT JOIN FETCH v.detalles
        WHERE v.idVenta = :id
    """)
    Optional<Venta> findVentaParaAnulacion(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT v
        FROM Venta v
        LEFT JOIN FETCH v.pago
    """)
    List<Venta> findAllConPago();
}
