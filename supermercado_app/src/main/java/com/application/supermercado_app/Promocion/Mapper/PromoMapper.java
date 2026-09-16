package com.application.supermercado_app.Promocion.Mapper;

import com.application.supermercado_app.Promocion.DTO.CrearPromocionDTO;
import com.application.supermercado_app.Promocion.DTO.PromocionDTO;
import com.application.supermercado_app.Promocion.Model.Promocion;

public class PromoMapper {

    public static PromocionDTO toDTO(Promocion promo) {
        if (promo == null) {
            return null;
        }

        return PromocionDTO.builder()
                .idPromocion(promo.getIdPromo())
                .descripcion(promo.getDescripcion())
                .activa(promo.isActiva())
                .tipo(promo.getTipo())
                .build();
    }

    public static Promocion toEntity(CrearPromocionDTO promo) {
        if (promo == null) {
            return null;
        }

        return Promocion.builder()
                .descripcion(promo.getDescripcion())
                .tipo(promo.getTipo())
                .activa(promo.isActiva())
                .build();
    }
}
