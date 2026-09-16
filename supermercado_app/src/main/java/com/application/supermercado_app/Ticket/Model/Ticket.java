package com.application.supermercado_app.Ticket.Model;

import com.application.supermercado_app.Venta.Model.Venta;
import com.application.supermercado_app.Pago.Model.MetodoPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

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
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTicket;

    @Column(unique = true)
    private String numeroTicket;

    private LocalDateTime fechaEmision;

    private Double total;

    private Double montoRecibido;
    private Double vuelto;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idVenta", unique = true)
    private Venta venta;

}
