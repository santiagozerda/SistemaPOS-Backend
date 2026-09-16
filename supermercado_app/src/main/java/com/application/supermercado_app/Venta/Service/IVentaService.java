package com.application.supermercado_app.Venta.Service;

import com.application.supermercado_app.Venta.DTO.CrearVentaRequestDTO;
import com.application.supermercado_app.Venta.DTO.VentaCompletaDTO;
import com.application.supermercado_app.Venta.DTO.VentaPreviewDTO;
import com.application.supermercado_app.Venta.DTO.VentaPreviewRequestDTO;
import com.application.supermercado_app.Venta.DTO.VentaResumenDTO;
import java.util.List;

public interface IVentaService {

    public List<VentaResumenDTO> getVentas();

    public VentaCompletaDTO saveVenta(CrearVentaRequestDTO crearVentaDTO);

    public VentaCompletaDTO anularVenta(Long idVenta);

    public VentaCompletaDTO cancelarVenta(Long idVenta);

    public VentaCompletaDTO findVenta(Long idVenta);
    
    public VentaPreviewDTO previewVenta(VentaPreviewRequestDTO previewDTO);
}
