package com.application.supermercado_app.Exception;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp,
        int status,
        String error,
        String message) {

}
