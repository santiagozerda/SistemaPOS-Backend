package com.application.supermercado_app.Pago.Model;

import com.application.supermercado_app.Venta.Model.Venta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;
    private Double monto;
    private Double vuelto;
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @OneToOne
    @JoinColumn(name = "idVenta", unique = true)
    private Venta venta;

    private String paymentId;
    private String preferenceId;
    private String externalReference;
    @Column(length = 5000)
    private String qrCode;

}
