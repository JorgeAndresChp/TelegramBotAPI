package com.telegrambotapi.strategy;

import com.telegrambotapi.adapter.AIServiceAdapter;
import com.telegrambotapi.adapter.AIServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Estrategia para técnicas de upselling.
 * Implementa el patrón Strategy para asesoramiento en upselling.
 */
@Component
public class UpsellingStrategy implements ResponseStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(UpsellingStrategy.class);
    
    @Autowired
    private AIServiceAdapter aiServiceAdapter;
    
    @Override
    public String generateResponse(String salesConversation) throws AIServiceException {
        logger.info("Generando estrategia de upselling");
        
        String objective = "realizar upselling sugiriendo productos mejores o adicionales que aporten valor al cliente";
        
        try {
            return aiServiceAdapter.analyzeSalesConversation(salesConversation, objective);
        } catch (AIServiceException e) {
            logger.error("Error generando estrategia de upselling", e);
            throw new AIServiceException("Error al generar consejos de upselling", e);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Upselling";
    }
    
    @Override
    public boolean isAvailable() {
        return aiServiceAdapter != null && aiServiceAdapter.isServiceAvailable();
    }
}