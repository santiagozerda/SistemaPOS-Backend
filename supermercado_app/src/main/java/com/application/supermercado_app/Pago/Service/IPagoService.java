package com.application.supermercado_app.Pago.Service;

import com.application.supermercado_app.Pago.DTO.PagoRequestDTO;
import com.application.supermercado_app.Pago.DTO.PagoResponseDTO;
import com.application.supermercado_app.Pago.DTO.PagoTransferenciaRequestDTO;
import com.application.supermercado_app.Pago.DTO.QRResponseDTO;

public interface IPagoService {

    public PagoResponseDTO procesarPago(PagoRequestDTO pagoRequestDTO);

    public PagoResponseDTO getPago(Long ventaID);

    public QRResponseDTO generarQR(PagoTransferenciaRequestDTO pagoQR);

}
