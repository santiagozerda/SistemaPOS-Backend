package com.application.supermercado_app.Producto.Mapper;

import com.application.supermercado_app.Producto.DTO.CrearProductoDTO;
import com.application.supermercado_app.Producto.DTO.ProductoDTO;
import com.application.supermercado_app.Producto.Model.Producto;
import com.application.supermercado_app.Promocion.Mapper.PromoMapper;

public class ProductoMapper {

    public static ProductoDTO toResponseDTO(Producto p) {

        if (p == null) {
            return null;
        }

        return ProductoDTO.builder()
                .id(p.getIdProductos())
                .nombre(p.getNombre())
                .categoria(p.getCategoria())
                .precio(p.getPrecio())
                .cantidad(p.getCantidad())
                .codigoBarra(p.getCodigoBarra())
                .promo(PromoMapper.toDTO(p.getPromo()))
                .build();

    }

    public static Producto toEntity(CrearProductoDTO p) {

        if (p == null) {
            return null;
        }

        return Producto.builder()
                .nombre(p.getNombre())
                .cantidad(p.getCantidad())
                .categoria(p.getCategoria())
                .precio(p.getPrecio())
                .codigoBarra(p.getCodigoBarra())
                .build();

    }

}
