
package com.application.supermercado_app.Exception;


public class ArchivoExcelException extends RuntimeException{
    
    public ArchivoExcelException(String message){
        super(message);
    }
    
    public ArchivoExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
