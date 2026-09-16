package com.application.supermercado_app.Reportes.Controller;

import com.application.supermercado_app.Reportes.DTO.ProductosMasVendidosDTO;
import com.application.supermercado_app.Reportes.DTO.ReporteVentaDTO;
import com.application.supermercado_app.Reportes.DTO.SemanaDelMesDTO;
import com.application.supermercado_app.Reportes.DTO.VentaDiaDTO;
import com.application.supermercado_app.Reportes.DTO.VentaMesDTO;
import com.application.supermercado_app.Reportes.DTO.VentaSemanaDTO;
import com.application.supermercado_app.Reportes.Service.IReporteVentaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ADMINISTRADOR')")
@RestController
@RequestMapping("/app/reporte")
public class ReporteVentaController {

    private final IReporteVentaService reporteVentaService;

    public ReporteVentaController(
            IReporteVentaService reporteVentaService) {

        this.reporteVentaService
                = reporteVentaService;
    }

    @GetMapping("/dia")
    public ResponseEntity<
            ReporteVentaDTO<VentaDiaDTO>> reporteDia() {

        return ResponseEntity.ok(
                reporteVentaService.obtenerReporteDia()
        );
    }

    @GetMapping("/semana")
    public ResponseEntity<
            ReporteVentaDTO<VentaSemanaDTO>> reporteSemana(
            @RequestParam int anio,
            @RequestParam int mes,
            @RequestParam int semana) {

        return ResponseEntity.ok(
                reporteVentaService.obtenerReporteSemana(
                        anio,
                        mes,
                        semana
                )
        );
    }

    @GetMapping("/mes")
    public ResponseEntity<
            ReporteVentaDTO<VentaMesDTO>> reporteMes(
            @RequestParam int anio,
            @RequestParam int mes) {

        return ResponseEntity.ok(
                reporteVentaService.obtenerReporteMes(
                        anio,
                        mes
                )
        );
    }

    @GetMapping("/semanas-del-mes")
    public ResponseEntity<List<SemanaDelMesDTO>>
            semanasDelMes(
                    @RequestParam int anio,
                    @RequestParam int mes) {

        return ResponseEntity.ok(
                reporteVentaService.obtenerSemanaDelMes(anio, mes)
        );
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductosMasVendidosDTO>>
            productosMasVendidos() {

        return ResponseEntity.ok(
                reporteVentaService.productosMasVendidos()
        );
    }
}
