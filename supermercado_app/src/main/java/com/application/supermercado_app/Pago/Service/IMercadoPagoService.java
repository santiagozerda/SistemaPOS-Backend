package com.application.supermercado_app.Pago.Service;

import com.application.supermercado_app.Pago.DTO.QRResponseDTO;
import com.application.supermercado_app.Pago.Model.Pago;
import com.application.supermercado_app.Venta.Model.Venta;

public interface IMercadoPagoService {

    public QRResponseDTO generarQR(Venta venta, Pago pago);
    
    public void procesarWebhook(Long paymentId);

}
