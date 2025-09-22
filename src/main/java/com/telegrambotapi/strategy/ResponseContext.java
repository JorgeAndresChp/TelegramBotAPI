package com.telegrambotapi.strategy;

import com.telegrambotapi.adapter.AIServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Contexto del patrón Strategy que gestiona las diferentes estrategias de respuesta.
 * Permite cambiar dinámicamente entre estrategias según el tipo de interacción requerida.
 */
@Component
public class ResponseContext {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseContext.class);
    
    @Autowired
    private JokeGenerationStrategy jokeGenerationStrategy;
    
    @Autowired
    private RefundRejectionStrategy refundRejectionStrategy;
    
    @Autowired
    private UpsellingStrategy upsellingStrategy;
    
    @Autowired
    private PurchaseMotivationStrategy purchaseMotivationStrategy;
    
    private final Map<StrategyType, ResponseStrategy> strategies = new HashMap<>();
    
    /**
     * Tipos de estrategias disponibles
     */
    public enum StrategyType {
        JOKE_GENERATION,
        REFUND_REJECTION,
        UPSELLING,
        PURCHASE_MOTIVATION
    }
    
    /**
     * Inicializa las estrategias después de la inyección de dependencias
     */
    public void initializeStrategies() {
        strategies.put(StrategyType.JOKE_GENERATION, jokeGenerationStrategy);
        strategies.put(StrategyType.REFUND_REJECTION, refundRejectionStrategy);
        strategies.put(StrategyType.UPSELLING, upsellingStrategy);
        strategies.put(StrategyType.PURCHASE_MOTIVATION, purchaseMotivationStrategy);
        
        logger.info("Estrategias inicializadas: {}", strategies.size());
    }
    
    /**
     * Ejecuta una estrategia específica con la entrada proporcionada
     * @param strategyType el tipo de estrategia a ejecutar
     * @param input la entrada para la estrategia
     * @return la respuesta generada por la estrategia
     * @throws AIServiceException si ocurre un error al ejecutar la estrategia
     */
    public String executeStrategy(StrategyType strategyType, String input) throws AIServiceException {
        if (strategies.isEmpty()) {
            initializeStrategies();
        }
        
        ResponseStrategy strategy = strategies.get(strategyType);
        
        if (strategy == null) {
            throw new AIServiceException("Estrategia no encontrada: " + strategyType);
        }
        
        if (!strategy.isAvailable()) {
            throw new AIServiceException("Estrategia no disponible: " + strategy.getStrategyName());
        }
        
        logger.info("Ejecutando estrategia: {}", strategy.getStrategyName());
        return strategy.generateResponse(input);
    }
    
    /**
     * Verifica si una estrategia específica está disponible
     * @param strategyType el tipo de estrategia a verificar
     * @return true si la estrategia está disponible, false en caso contrario
     */
    public boolean isStrategyAvailable(StrategyType strategyType) {
        if (strategies.isEmpty()) {
            initializeStrategies();
        }
        
        ResponseStrategy strategy = strategies.get(strategyType);
        return strategy != null && strategy.isAvailable();
    }
    
    /**
     * Obtiene información sobre todas las estrategias disponibles
     * @return un mapa con el estado de disponibilidad de cada estrategia
     */
    public Map<StrategyType, Boolean> getStrategiesStatus() {
        if (strategies.isEmpty()) {
            initializeStrategies();
        }
        
        Map<StrategyType, Boolean> status = new HashMap<>();
        for (Map.Entry<StrategyType, ResponseStrategy> entry : strategies.entrySet()) {
            status.put(entry.getKey(), entry.getValue().isAvailable());
        }
        return status;
    }
    
    /**
     * Obtiene el nombre de una estrategia específica
     * @param strategyType el tipo de estrategia
     * @return el nombre de la estrategia o null si no existe
     */
    public String getStrategyName(StrategyType strategyType) {
        if (strategies.isEmpty()) {
            initializeStrategies();
        }
        
        ResponseStrategy strategy = strategies.get(strategyType);
        return strategy != null ? strategy.getStrategyName() : null;
    }
}