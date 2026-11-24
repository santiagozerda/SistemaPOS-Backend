package com.application.supermercado_app.Producto.Repository;

import com.application.supermercado_app.Producto.Model.DetalleImportacionProducto;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetalleImportacionProductoRepository {

    // Recuperar las filas de un lote incluyendo sus errores.
    // Los resultados se ordenan según el número de fila del Excel.
    @Query("""
        SELECT DISTINCT d
        FROM DetalleImportacionProducto d
        LEFT JOIN FETCH d.errores
        WHERE d.lote.id = :loteId
        ORDER BY d.numeroFila ASC
    """)
    List<DetalleImportacionProducto> findAllByLoteIdWithErrores(
            @Param("loteId") Long loteId
    );

    // Recuperar todas las filas de un lote ordenadas.
    List<DetalleImportacionProducto>
            findByLote_IdOrderByNumeroFilaAsc(Long loteId);
}
