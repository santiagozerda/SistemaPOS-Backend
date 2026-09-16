package com.application.supermercado_app.Pago.Repository;

import com.application.supermercado_app.Pago.Model.Pago;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    //Buscar el Id de la venta
    Optional<Pago> findByVentaIdVenta(Long idVenta);

    Optional<Pago> findByPaymentId(String paymentId);

    Optional<Pago> findByExternalReference(String externalReference);

}
