package com.application.supermercado_app.Auth.Controller;

import com.application.supermercado_app.Auth.DTO.AuthResult;
import com.application.supermercado_app.Auth.DTO.LoginRequestDTO;
import com.application.supermercado_app.Auth.DTO.LoginResponseDTO;
import com.application.supermercado_app.Auth.Service.IAuthService;
import com.application.supermercado_app.Config.Security.CustomUserDetails;
import com.application.supermercado_app.Usuario.Model.Usuario;
import java.time.Duration;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO login) {

        AuthResult authResult = authService.login(login);

        ResponseCookie jwtCookie = createJwtCookie(
                authResult.getToken(),
                Duration.ofHours(8)
        );

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
    public ResponseEntity<LoginResponseDTO> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = userDetails.getUsuario();

        LoginResponseDTO response = new LoginResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie deleteCookie = createJwtCookie(
                "",
                Duration.ZERO
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    private ResponseCookie createJwtCookie(
            String token,
            Duration maxAge) {

        return ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
    }
}
