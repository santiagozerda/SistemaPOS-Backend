package com.application.supermercado_app.Producto.Controller;

import com.application.supermercado_app.Producto.DTO.Excel.AnalisisImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ConfirmarImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ResultadoImportacionDTO;
import com.application.supermercado_app.Producto.Service.IProductoExcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/app/productos/excel")
@RequiredArgsConstructor
public class ProductoExcelController {

    private final IProductoExcelService productoExcelService;

    // =====================================================
    // ANALIZAR ARCHIVO EXCEL
    // =====================================================
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping(
            value = "/analizar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AnalisisImportacionDTO> analizarArchivo(
            @RequestParam("archivo") MultipartFile archivo) {

        AnalisisImportacionDTO resultado
                = productoExcelService.analizarArchivo(archivo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resultado);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/confirmar")
    public ResponseEntity<ResultadoImportacionDTO> confirmarImportacion(
            @Valid @RequestBody ConfirmarImportacionDTO confirmacion) {

        ResultadoImportacionDTO resultado
                = productoExcelService.confirmarImportacion(confirmacion);

        return ResponseEntity.ok(resultado);
    }
}
