package com.application.supermercado_app.Usuario.Service;

import com.application.supermercado_app.Exception.NotFoundException;
import com.application.supermercado_app.Usuario.DTO.CrearUsuarioDTO;
import com.application.supermercado_app.Usuario.DTO.UsuarioDTO;
import com.application.supermercado_app.Usuario.Mapper.UsuarioMapper;
import com.application.supermercado_app.Usuario.Model.Usuario;
import com.application.supermercado_app.Usuario.Repository.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements IUsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UsuarioDTO crearUsuario(CrearUsuarioDTO usuario) {
        if (usuarioRepo.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con este email");
        }
        
        Usuario user = Usuario.builder()
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .password(passwordEncoder.encode(usuario.getPassword()))
                .rol(usuario.getRol())
                .activo(true)
                .build();
        
        return UsuarioMapper.toDTO(usuarioRepo.save(user));
        
    }
    
    @Override
    public List<UsuarioDTO> getUsuarios() {
        return usuarioRepo.findAll()
                .stream()
                .map(UsuarioMapper::toDTO)
                .toList();
    }
    
    @Override
    public UsuarioDTO findUsuario(Long idUsuario) {
        Usuario user = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        return UsuarioMapper.toDTO(user);
    }
    
    @Override
    public UsuarioDTO editUsuario(Long idUsuario, CrearUsuarioDTO usuario) {
        
        Usuario user = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        if (usuarioRepo.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con este email");
        }
        
        user.setNombre(usuario.getNombre());
        user.setEmail(usuario.getEmail());
        user.setRol(usuario.getRol());
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        
        return UsuarioMapper.toDTO(usuarioRepo.save(user));
    }
    
    @Override
    public void deleteUsuario(Long idUsuario) {
        Usuario user = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        user.setActivo(false);
        
        usuarioRepo.save(user);
    }
    
}
