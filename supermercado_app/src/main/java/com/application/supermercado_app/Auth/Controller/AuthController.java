package com.application.supermercado_app.Auth.Controller;

import com.application.supermercado_app.Auth.DTO.AuthResult;
import com.application.supermercado_app.Auth.DTO.LoginRequestDTO;
import com.application.supermercado_app.Auth.DTO.LoginResponseDTO;
import com.application.supermercado_app.Auth.Service.IAuthService;
import com.application.supermercado_app.Config.Security.CustomUserDetails;
import com.application.supermercado_app.Usuario.Model.Usuario;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO login) {

        AuthResult authResult = authService.login(login);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", authResult.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(8))
                .sameSite("Lax")
                .build();

        Usuario usuario = authResult.getUsuario();

        LoginResponseDTO response = new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);

    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> getCurrentUser(Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Usuario usuario = userDetails.getUsuario();

        LoginResponseDTO response = new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}
