package com.application.supermercado_app.Auth.Service;

import com.application.supermercado_app.Auth.DTO.AuthResult;
import com.application.supermercado_app.Auth.DTO.LoginRequestDTO;
import com.application.supermercado_app.Config.Security.CustomUserDetails;
import com.application.supermercado_app.Config.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResult login(LoginRequestDTO login) {

        Authentication authentication
                = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                login.getEmail(),
                                login.getPassword()
                        )
                );

        CustomUserDetails userDetails
                = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new AuthResult(
                token,
                userDetails.getUsuario()
        );
    }
}
