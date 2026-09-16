
package com.application.supermercado_app.Usuario.Service;

import com.application.supermercado_app.Usuario.DTO.CrearUsuarioDTO;
import com.application.supermercado_app.Usuario.DTO.UsuarioDTO;
import java.util.List;


public interface IUsuarioService {
    
    public UsuarioDTO crearUsuario(CrearUsuarioDTO usuario);
    
    public List<UsuarioDTO> getUsuarios();
    
    public UsuarioDTO findUsuario(Long idUsuario);
    
    public UsuarioDTO editUsuario(Long idUsuario, CrearUsuarioDTO usuario);
    
    public void deleteUsuario(Long idUsuario);
}
