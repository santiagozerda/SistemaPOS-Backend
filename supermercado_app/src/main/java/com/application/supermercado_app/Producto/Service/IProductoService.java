package com.application.supermercado_app.Producto.Service;

import com.application.supermercado_app.Producto.DTO.AjustarStockDTO;
import com.application.supermercado_app.Producto.DTO.CrearProductoDTO;
import com.application.supermercado_app.Producto.DTO.Excel.AnalisisImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ConfirmarImportacionDTO;
import com.application.supermercado_app.Producto.DTO.Excel.ResultadoImportacionDTO;
import com.application.supermercado_app.Promocion.DTO.AsignarPromocionDTO;
import com.application.supermercado_app.Producto.DTO.ProductoDTO;
import com.application.supermercado_app.Producto.DTO.ResultadoActualizacionProductoDTO;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IProductoService {

    public List<ProductoDTO> getProductos();

    public ProductoDTO saveProducto(CrearProductoDTO crearProducDTO);

    public void deleteProducto(Long id);

    public ProductoDTO editProducto(Long id, CrearProductoDTO crearProductoDTO);

    public ProductoDTO asignarPromocion(AsignarPromocionDTO promocionDTO);

    public ProductoDTO findProducto(Long id);

    public List<ProductoDTO> productosConBajoStock();

    public ProductoDTO ajustaStock(AjustarStockDTO stockDTO);

    public ProductoDTO buscarCodigoBarra(String codigoBarra);

    public List<ProductoDTO> buscarPorCodigoCorto(String prefijo);

    public ProductoDTO crearProductoDesdeImportacion(String nombre,
            String categoria,
            BigDecimal precio,
            Integer cantidad,
            String codigoBarra);

    public ResultadoActualizacionProductoDTO actualizarDesdeImportacion(
            Long idProducto,
            String nombreExcel,
            String codigoBarraExcel,
            String codigoBarraDectectado,
            boolean coincidenciaPorNombre,
            Integer unidadesIngresadas,
            BigDecimal precioExcel,
            BigDecimal precioActualDetectado,
            boolean actualizarPrecio
    );

}
