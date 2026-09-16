package com.application.supermercado_app.Venta.Repository;

import com.application.supermercado_app.Reportes.DTO.ProductosMasVendidosDTO;
import com.application.supermercado_app.Venta.Model.DetalleVentas;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentasRepository extends JpaRepository<DetalleVentas, Long> {

    @Query("""
        SELECT new com.application.supermercado_app.Reportes.DTO.ProductosMasVendidosDTO(
            d.productos.idProductos,
            d.productos.nombre,
            SUM(d.cantidadVendida)
        )
        FROM DetalleVentas d
        WHERE d.venta.estado = :estado
        GROUP BY d.productos.idProductos, d.productos.nombre
        ORDER BY SUM(d.cantidadVendida) DESC
    """)
    List<ProductosMasVendidosDTO> topProductosMasVendidos(
            @Param("estado") EstadoVenta estado,
            Pageable pageable
    );
}
