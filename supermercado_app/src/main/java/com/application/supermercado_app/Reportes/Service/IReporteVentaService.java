package com.application.supermercado_app.Reportes.Service;

import com.application.supermercado_app.Reportes.DTO.ProductosMasVendidosDTO;
import com.application.supermercado_app.Reportes.DTO.ReporteVentaDTO;
import com.application.supermercado_app.Reportes.DTO.SemanaDelMesDTO;
import com.application.supermercado_app.Reportes.DTO.VentaDiaDTO;
import com.application.supermercado_app.Reportes.DTO.VentaMesDTO;
import com.application.supermercado_app.Reportes.DTO.VentaSemanaDTO;
import java.util.List;

public interface IReporteVentaService {

    public ReporteVentaDTO<VentaDiaDTO> obtenerReporteDia();

    public ReporteVentaDTO<VentaSemanaDTO> obtenerReporteSemana(int anio, int mes, int semana);

    public ReporteVentaDTO<VentaMesDTO> obtenerReporteMes(int anio, int mes);
    
    public List<ProductosMasVendidosDTO> productosMasVendidos();
    
    public List<SemanaDelMesDTO> obtenerSemanaDelMes(int anio, int mes);
}
