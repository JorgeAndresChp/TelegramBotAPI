package com.telegrambotapi.adapter;

/**
 * Excepción personalizada para errores del servicio de IA
 */
public class AIServiceException extends Exception {
    
    public AIServiceException(String message) {
        super(message);
    }
    
    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}