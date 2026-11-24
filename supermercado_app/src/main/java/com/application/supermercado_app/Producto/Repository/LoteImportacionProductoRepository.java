package com.application.supermercado_app.Producto.Repository;

import com.application.supermercado_app.Producto.Model.LoteImportacionProducto;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoteImportacionProductoRepository extends JpaRepository<LoteImportacionProducto, Long> {

    // Buscar un lote mediante su identificador público.
    Optional<LoteImportacionProducto> findByLoteUuid(UUID loteUuid);

    // Comprobar si existe un lote con determinado UUID.
    boolean existsByLoteUuid(UUID loteUuid);

    // Recuperar y bloquear el lote durante una confirmación.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT l
        FROM LoteImportacionProducto l
        WHERE l.loteUuid = :loteUuid
    """)
    Optional<LoteImportacionProducto> findByLoteUuidForUpdate(
            @Param("loteUuid") UUID loteUuid
    );
}
