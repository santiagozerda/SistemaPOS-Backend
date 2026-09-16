package com.application.supermercado_app.Reportes.Mapper;

import com.application.supermercado_app.Reportes.DTO.VentaDiaDTO;
import com.application.supermercado_app.Reportes.DTO.VentaMesDTO;
import com.application.supermercado_app.Reportes.DTO.VentaSemanaDTO;
import com.application.supermercado_app.Venta.Model.Venta;
import java.time.LocalDate;

public class ReporteVentaMapper {

    public static VentaDiaDTO toVentaDiaDTO(Venta venta) {

        if (venta == null) {
            return null;
        }

        return VentaDiaDTO.builder()
                .numeroTicket(
                        venta.getTicket().getNumeroTicket()
                )
                .metodoPago(
                        venta.getPago().getMetodoPago()
                )
                .total(
                        venta.getTotal()
                )
                .build();
    }

    public static VentaSemanaDTO toVentaSemanaDTO(
            LocalDate dia,
            Long cantidadVentas,
            Double total) {

        return VentaSemanaDTO.builder()
                .dia(dia)
                .cantidadVentas(cantidadVentas)
                .total(total)
                .build();
    }

    public static VentaMesDTO toVentaMesDTO(
            Integer semana,
            Long cantidadVentas,
            Double importe) {

        return VentaMesDTO.builder()
                .cantidadVentas(cantidadVentas)
                .semana(semana)
                .total(importe)
                .build();
    }
}
