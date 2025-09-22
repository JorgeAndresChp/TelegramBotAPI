package com.telegrambotapi.strategy;

import com.telegrambotapi.adapter.AIServiceException;

/**
 * Interfaz del patrón Strategy para diferentes estrategias de respuesta.
 * Permite cambiar dinámicamente la lógica de respuesta según el objetivo específico.
 */
public interface ResponseStrategy {
    
    /**
     * Genera una respuesta basada en la entrada proporcionada
     * @param input la entrada del usuario o contexto
     * @return la respuesta generada según la estrategia
     * @throws AIServiceException si ocurre un error al generar la respuesta
     */
    String generateResponse(String input) throws AIServiceException;
    
    /**
     * Obtiene el nombre de la estrategia
     * @return el nombre descriptivo de la estrategia
     */
    String getStrategyName();
    
    /**
     * Verifica si la estrategia está disponible y configurada correctamente
     * @return true si la estrategia está disponible, false en caso contrario
     */
    boolean isAvailable();
}