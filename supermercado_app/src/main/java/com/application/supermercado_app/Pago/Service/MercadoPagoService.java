package com.application.supermercado_app.Pago.Service;

import com.application.supermercado_app.Pago.DTO.QRResponseDTO;
import com.application.supermercado_app.Pago.Model.EstadoPago;
import com.application.supermercado_app.Pago.Model.Pago;
import com.application.supermercado_app.Pago.Repository.PagoRepository;
import com.application.supermercado_app.Ticket.Service.TicketService;
import com.application.supermercado_app.Venta.Model.Venta;
import com.application.supermercado_app.Venta.Service.VentaService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MercadoPagoService implements IMercadoPagoService {

    @Autowired
    private PagoRepository pagoRepo;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private TicketService ticketService;

    @Value("${mercadopago.notification-url}")
    private String notificationUrl;

    @Override
    public QRResponseDTO generarQR(Venta venta, Pago pago) {

        try {

            PreferenceItemRequest item
                    = PreferenceItemRequest.builder()
                            .title("Venta Supermercado #" + venta.getIdVenta())
                            .quantity(1)
                            .currencyId("ARS")
                            .unitPrice(BigDecimal.valueOf(venta.getTotal()))
                            .build();

            PreferenceBackUrlsRequest backUrls
                    = PreferenceBackUrlsRequest.builder()
                            .success("http://localhost:5173/pago-exitoso")
                            .failure("http://localhost:5173/pago-error")
                            .pending("http://localhost:5173/pago-pendiente")
                            .build();

            PreferenceRequest preferenceRequest
                    = PreferenceRequest.builder()
                            .items(List.of(item))
                            .externalReference(pago.getExternalReference())
                            .backUrls(backUrls)
                            .notificationUrl(notificationUrl)
                            .build();

            PreferenceClient client = new PreferenceClient();

            Preference preference = client.create(preferenceRequest);

            pago.setPreferenceId(preference.getId());

            pago.setQrCode(preference.getSandboxInitPoint());

            pagoRepo.save(pago);

            return QRResponseDTO.builder()
                    .ventaId(venta.getIdVenta())
                    .pagoId(pago.getIdPago())
                    .estadoPago(pago.getEstado())
                    .preferenceId(preference.getId())
                    .initPoint(preference.getSandboxInitPoint())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el QR", e);

        }
    }

    @Override
    public void procesarWebhook(Long paymentId) {

        try {

            PaymentClient paymentClient
                    = new PaymentClient();

            Payment payment
                    = paymentClient.get(paymentId);

            if (!"approved".equals(payment.getStatus())) {
                return;
            }

            Pago pago
                    = pagoRepo.findByExternalReference(
                            payment.getExternalReference()
                    )
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Pago no encontrado"
                                    )
                            );

            if (pago.getEstado()
                    == EstadoPago.APROBADO) {

                return;
            }

            pago.setPaymentId(
                    String.valueOf(
                            payment.getId()
                    )
            );

            pago.setEstado(
                    EstadoPago.APROBADO
            );

            pagoRepo.save(pago);

            Venta venta = pago.getVenta();

            ventaService.aprobarVenta(
                    venta
            );

            ticketService.createTicket(
                    venta
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error procesando webhook", e);
        }
    }

}
