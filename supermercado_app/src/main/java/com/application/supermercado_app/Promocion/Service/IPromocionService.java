package com.application.supermercado_app.Promocion.Service;

import com.application.supermercado_app.Promocion.DTO.PromocionDTO;
import com.application.supermercado_app.Producto.Model.Producto;
import com.application.supermercado_app.Promocion.DTO.CrearPromocionDTO;
import java.util.List;

public interface IPromocionService {

    public Double calcularTotal(Producto producto, int cantidad);

    public PromocionDTO createPromocion(CrearPromocionDTO promo);

    public List<PromocionDTO> getPromociones();

    public PromocionDTO findPromocion(Long id);

    public PromocionDTO editPromo(Long id, CrearPromocionDTO promo);

    public void deletePromo(Long id);
}
