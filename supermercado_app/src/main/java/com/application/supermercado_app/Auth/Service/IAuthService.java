
package com.application.supermercado_app.Auth.Service;

import com.application.supermercado_app.Auth.DTO.AuthResult;
import com.application.supermercado_app.Auth.DTO.LoginRequestDTO;


public interface IAuthService {
    
    public AuthResult login(LoginRequestDTO login);
}
