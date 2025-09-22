package com.telegrambotapi.strategy;

import com.telegrambotapi.adapter.AIServiceAdapter;
import com.telegrambotapi.adapter.AIServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Estrategia para rechazar devoluciones de manera efectiva.
 * Implementa el patrón Strategy para asesoramiento en rechazo de devoluciones.
 */
@Component
public class RefundRejectionStrategy implements ResponseStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(RefundRejectionStrategy.class);
    
    @Autowired
    private AIServiceAdapter aiServiceAdapter;
    
    @Override
    public String generateResponse(String salesConversation) throws AIServiceException {
        logger.info("Generando estrategia para rechazar devolución");
        
        String objective = "rechazar una devolución de manera diplomática y mantener la relación con el cliente";
        
        try {
            return aiServiceAdapter.analyzeSalesConversation(salesConversation, objective);
        } catch (AIServiceException e) {
            logger.error("Error generando estrategia de rechazo de devolución", e);
            throw new AIServiceException("Error al generar consejos para rechazar devolución", e);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Rechazo de Devolución";
    }
    
    @Override
    public boolean isAvailable() {
        return aiServiceAdapter != null && aiServiceAdapter.isServiceAvailable();
    }
}