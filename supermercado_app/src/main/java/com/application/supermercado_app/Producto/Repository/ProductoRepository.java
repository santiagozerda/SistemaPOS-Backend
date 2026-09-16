package com.application.supermercado_app.Producto.Repository;

import com.application.supermercado_app.Producto.Model.Producto;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.promo WHERE p.idProductos = :id")
    Optional<Producto> findByIdWithPromo(@Param("id") Long id);

    //Buscar los productos con bajo Stock
    List<Producto> findByCantidadLessThanEqual(Integer cantidad);

    //Buscar los productos por su codigo de barra
    Optional<Producto> findByCodigoBarra(String codigo);

    boolean existsByCodigoBarra(String codigoBarra);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.promo")
    List<Producto> findAllConPromo();

    List<Producto> findByCodigoCortoStartingWith(String prefijo);

    Optional<Producto> findByCodigoCorto(String codigoCorto);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p
        FROM Producto p
        WHERE p.idProductos = :id
    """)
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);
    
    boolean existsByCodigoCorto(String codigoCorto);
}
