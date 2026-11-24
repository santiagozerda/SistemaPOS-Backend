package com.application.supermercado_app.Producto.Service;

import com.application.supermercado_app.Producto.DTO.Excel.AnalisisImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ConfirmarImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ResultadoImportacionDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IProductoExcelService {
    
    public AnalisisImportacionDTO analizarArchivo(
            MultipartFile archivo
    );

    public ResultadoImportacionDTO confirmarImportacion(
            ConfirmarImportacionDTO confirmacion
    );
}
