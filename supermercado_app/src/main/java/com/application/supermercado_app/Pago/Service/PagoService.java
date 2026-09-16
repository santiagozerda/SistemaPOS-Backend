package com.application.supermercado_app.Pago.Service;

import com.application.supermercado_app.Exception.PagoException;
import com.application.supermercado_app.Exception.VentaException;
import com.application.supermercado_app.Pago.DTO.PagoRequestDTO;
import com.application.supermercado_app.Pago.DTO.PagoResponseDTO;
import com.application.supermercado_app.Pago.DTO.PagoTransferenciaRequestDTO;
import com.application.supermercado_app.Pago.DTO.QRResponseDTO;
import com.application.supermercado_app.Pago.Mapper.PagoMapper;
import com.application.supermercado_app.Pago.Model.EstadoPago;
import com.application.supermercado_app.Venta.Model.EstadoVenta;
import com.application.supermercado_app.Pago.Model.MetodoPago;
import com.application.supermercado_app.Pago.Model.Pago;
import com.application.supermercado_app.Venta.Model.Venta;
import com.application.supermercado_app.Pago.Repository.PagoRepository;
import com.application.supermercado_app.Ticket.DTO.TicketDTO;
import com.application.supermercado_app.Ticket.DTO.TicketReferenciaDTO;
import com.application.supermercado_app.Ticket.Service.TicketService;
import com.application.supermercado_app.Venta.Repository.VentaRepository;
import com.application.supermercado_app.Venta.Service.VentaService;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PagoService implements IPagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final VentaService ventaService;
    private final TicketService ticketService;
    private final MercadoPagoService mercadoPagoService;

    public PagoService(
            PagoRepository pagoRepository,
            VentaRepository ventaRepository,
            VentaService ventaService,
            TicketService ticketService,
            MercadoPagoService mercadoPagoService) {

        this.pagoRepository = pagoRepository;
        this.ventaRepository = ventaRepository;
        this.ventaService = ventaService;
        this.ticketService = ticketService;
        this.mercadoPagoService = mercadoPagoService;
    }

    @Override
    public PagoResponseDTO procesarPago(
            PagoRequestDTO pagoRequestDTO) {

        validarPago(pagoRequestDTO);

        Venta venta = obtenerVenta(
                pagoRequestDTO.getVentaId()
        );

        validarEstadoVenta(venta);

        validarMontoPago(
                venta,
                pagoRequestDTO
        );

        verificarVentaSinPago(
                venta.getIdVenta()
        );

        Pago pago = PagoMapper.toEntity(
                pagoRequestDTO,
                venta
        );

        procesarMetodoPago(
                pago,
                pagoRequestDTO
        );

        pago = pagoRepository.save(pago);

        venta.setPago(pago);

        TicketDTO ticket = null;

        if (pago.getEstado()
                == EstadoPago.APROBADO) {

            ticket = confirmarVenta(
                    venta,
                    pago
            );
        }

        PagoResponseDTO response
                = PagoMapper.toResponseDTO(pago);

        /*
         * El ticket ya fue generado en este punto.
         */
        if (ticket != null) {

            response.setTicket(
                    TicketReferenciaDTO.builder()
                            .idTicket(
                                    ticket.getIdTicket()
                            )
                            .numeroTicket(
                                    ticket.getNumeroTicket()
                            )
                            .build()
            );
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO getPago(
            Long ventaId) {

        if (ventaId == null) {

            throw new PagoException(
                    "Debe indicar la venta."
            );
        }

        Pago pago
                = pagoRepository
                        .findByVentaIdVenta(ventaId)
                        .orElseThrow(()
                                -> new PagoException(
                                "La venta no posee "
                                + "un pago asociado."
                        ));

        return PagoMapper.toResponseDTO(pago);
    }

    @Override
    public QRResponseDTO generarQR(
            PagoTransferenciaRequestDTO pagoQR) {

        if (pagoQR == null
                || pagoQR.getVentaId() == null) {

            throw new PagoException(
                    "Debe indicar la venta."
            );
        }

        Venta venta = obtenerVenta(
                pagoQR.getVentaId()
        );

        validarEstadoVenta(venta);

        verificarVentaSinPago(
                venta.getIdVenta()
        );

        Pago pago = Pago.builder()
                .venta(venta)
                .monto(venta.getTotal())
                .fechaPago(LocalDate.now())
                .metodoPago(
                        MetodoPago.TRANSFERENCIA
                )
                .estado(
                        EstadoPago.PENDIENTE
                )
                .vuelto(0.0)
                .externalReference(
                        "VENTA_"
                        + venta.getIdVenta()
                )
                .build();

        pago = pagoRepository.save(pago);

        return mercadoPagoService.generarQR(
                venta,
                pago
        );
    }

    private void procesarMetodoPago(
            Pago pago,
            PagoRequestDTO request) {

        switch (pago.getMetodoPago()) {

            case EFECTIVO:

                if (request.getMontoEntregado() == null
                        || request.getMontoEntregado()
                        < pago.getMonto()) {

                    pago.setEstado(
                            EstadoPago.RECHAZADO
                    );

                    pago.setVuelto(0.0);

                } else {

                    pago.setEstado(
                            EstadoPago.APROBADO
                    );

                    pago.setVuelto(
                            request.getMontoEntregado()
                            - pago.getMonto()
                    );
                }

                break;

            case TRANSFERENCIA:

                pago.setEstado(
                        EstadoPago.PENDIENTE
                );

                pago.setVuelto(0.0);

                break;

            default:

                pago.setEstado(
                        EstadoPago.RECHAZADO
                );

                pago.setVuelto(0.0);

                break;
        }
    }

    /**
     * Aprueba la Venta y genera su Ticket dentro de la misma transacción.
     */
    private TicketDTO confirmarVenta(
            Venta venta,
            Pago pago) {

        if (pago.getEstado()
                != EstadoPago.APROBADO) {

            return null;
        }

        ventaService.aprobarVenta(venta);

        return ticketService.createTicket(venta);
    }

    private void validarPago(
            PagoRequestDTO pago) {

        if (pago == null) {

            throw new PagoException(
                    "El pago no puede ser nulo."
            );
        }

        if (pago.getVentaId() == null) {

            throw new PagoException(
                    "Debe indicar la venta."
            );
        }

        if (pago.getTotalPagar() == null
                || pago.getTotalPagar() <= 0) {

            throw new PagoException(
                    "El total a pagar "
                    + "debe ser mayor que cero."
            );
        }

        if (pago.getMetodoPago() == null) {

            throw new PagoException(
                    "Debe indicar el método de pago."
            );
        }

        if (pago.getMetodoPago()
                == MetodoPago.EFECTIVO) {

            if (pago.getMontoEntregado() == null
                    || pago.getMontoEntregado() <= 0) {

                throw new PagoException(
                        "Debe indicar el monto "
                        + "entregado por el cliente."
                );
            }
        }
    }

    private void validarMontoPago(
            Venta venta,
            PagoRequestDTO pago) {

        if (Double.compare(
                venta.getTotal(),
                pago.getTotalPagar()
        ) != 0) {

            throw new PagoException(
                    "El monto del pago no coincide "
                    + "con el total de la venta."
            );
        }
    }

    private void verificarVentaSinPago(
            Long ventaId) {

        if (pagoRepository
                .findByVentaIdVenta(ventaId)
                .isPresent()) {

            throw new PagoException(
                    "La venta ya posee un pago asociado."
            );
        }
    }

    private void validarEstadoVenta(
            Venta venta) {

        if (venta.getEstado()
                == EstadoVenta.APROBADA) {

            throw new VentaException(
                    "La venta ya fue pagada."
            );
        }

        if (venta.getEstado()
                == EstadoVenta.CANCELADA) {

            throw new VentaException(
                    "La venta se encuentra cancelada."
            );
        }

        if (venta.getEstado()
                == EstadoVenta.ANULADA) {

            throw new VentaException(
                    "La venta se encuentra anulada."
            );
        }
    }

    private Venta obtenerVenta(
            Long idVenta) {

        if (idVenta == null) {

            throw new PagoException(
                    "El ID de la venta no puede ser nulo."
            );
        }

        return ventaRepository.findById(idVenta)
                .orElseThrow(()
                        -> new VentaException(
                        "Venta no encontrada."
                ));
    }
}
