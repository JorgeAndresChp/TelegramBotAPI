package com.telegrambotapi.strategy;

import com.telegrambotapi.adapter.AIServiceAdapter;
import com.telegrambotapi.adapter.AIServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Estrategia para motivar la compra de productos específicos.
 * Implementa el patrón Strategy para asesoramiento en motivación de compras.
 */
@Component
public class PurchaseMotivationStrategy implements ResponseStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(PurchaseMotivationStrategy.class);
    
    @Autowired
    private AIServiceAdapter aiServiceAdapter;
    
    @Override
    public String generateResponse(String salesConversation) throws AIServiceException {
        logger.info("Generando estrategia de motivación de compra");
        
        String objective = "motivar al cliente a realizar la compra destacando beneficios y creando urgencia apropiada";
        
        try {
            return aiServiceAdapter.analyzeSalesConversation(salesConversation, objective);
        } catch (AIServiceException e) {
            logger.error("Error generando estrategia de motivación de compra", e);
            throw new AIServiceException("Error al generar consejos de motivación de compra", e);
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Motivación de Compra";
    }
    
    @Override
    public boolean isAvailable() {
        return aiServiceAdapter != null && aiServiceAdapter.isServiceAvailable();
    }
}