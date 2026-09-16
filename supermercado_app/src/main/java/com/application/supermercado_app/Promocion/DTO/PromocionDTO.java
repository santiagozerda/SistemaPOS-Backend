package com.application.supermercado_app.Promocion.DTO;

import com.application.supermercado_app.Promocion.Model.TipoPromocion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromocionDTO {

    private Long idPromocion;
    private String descripcion;
    private TipoPromocion tipo;
    private boolean activa;

}
