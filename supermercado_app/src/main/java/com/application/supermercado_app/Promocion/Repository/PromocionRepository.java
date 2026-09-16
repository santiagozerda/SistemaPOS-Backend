package com.application.supermercado_app.Promocion.Repository;

import com.application.supermercado_app.Promocion.Model.Promocion;
import com.application.supermercado_app.Promocion.Model.TipoPromocion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    Optional<Promocion> findByTipo(TipoPromocion tipo);
}
