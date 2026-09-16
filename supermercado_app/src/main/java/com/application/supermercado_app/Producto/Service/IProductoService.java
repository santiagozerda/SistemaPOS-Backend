package com.application.supermercado_app.Producto.Service;

import com.application.supermercado_app.Producto.DTO.AjustarStockDTO;
import com.application.supermercado_app.Producto.DTO.CrearProductoDTO;
import com.application.supermercado_app.Promocion.DTO.AsignarPromocionDTO;
import com.application.supermercado_app.Producto.DTO.ProductoDTO;
import java.util.List;

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
}
