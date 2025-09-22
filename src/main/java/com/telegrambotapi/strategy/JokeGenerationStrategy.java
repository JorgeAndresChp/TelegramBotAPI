package com.telegrambotapi.strategy;

import com.telegrambotapi.adapter.AIServiceAdapter;
import com.telegrambotapi.adapter.AIServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Estrategia para generar chistes basados en el contexto de conversación.
 * Implementa el patrón Strategy para la generación de humor contextual.
 */
@Component
public class JokeGenerationStrategy implements ResponseStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(JokeGenerationStrategy.class);
    
    @Autowired
    private AIServiceAdapter aiServiceAdapter;
    
    @Override
    public String generateResponse(String conversationContext) throws AIServiceException {
        logger.info("Generando chiste basado en contexto: {}", 
                   conversationContext.substring(0, Math.min(100, conversationContext.length())));
        
        try {
            String joke = aiServiceAdapter.generateJoke(conversationContext);
            
            // Validar que la respuesta sea apropiada
            if (joke != null && !joke.trim().isEmpty()) {
                return "😄 " + joke;
            } else {
                return "😅 Lo siento, no se me ocurre un buen chiste en este momento. ¡Pero sigan con la conversación interesante!";
            }
            
        } catch (AIServiceException e) {
            logger.error("Error generando chiste", e);
            throw new AIServiceException("No pude generar un chiste en este momento", e);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Generación de Chistes";
    }
    
    @Override
    public boolean isAvailable() {
        return aiServiceAdapter != null && aiServiceAdapter.isServiceAvailable();
    }
    
    /**
     * Valida si el contexto es apropiado para generar un chiste
     * @param context el contexto de la conversación
     * @return true si es apropiado generar un chiste
     */
    public boolean isAppropriateForJoke(String context) {
        if (context == null || context.trim().isEmpty()) {
            return false;
        }
        
        // Evitar chistes en contextos sensibles
        String lowerContext = context.toLowerCase();
        String[] sensitiveTopics = {"muerte", "enfermedad", "accidente", "problema", "triste", "dolor"};
        
        for (String topic : sensitiveTopics) {
            if (lowerContext.contains(topic)) {
                return false;
            }
        }
        
        return true;
    }
}