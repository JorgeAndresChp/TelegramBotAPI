package com.telegrambotapi.service;

import com.telegrambotapi.adapter.AIServiceException;
import com.telegrambotapi.strategy.JokeGenerationStrategy;
import com.telegrambotapi.strategy.ResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para manejar el bot de chistes que analiza conversaciones grupales
 * y genera chistes cada 3-4 mensajes basados en el contexto.
 */
@Service
public class JokeBotService {
    
    private static final Logger logger = LoggerFactory.getLogger(JokeBotService.class);
    private static final int MIN_MESSAGES_FOR_JOKE = 3;
    private static final int MAX_MESSAGES_FOR_JOKE = 4;
    private static final int MAX_CONTEXT_LENGTH = 1000; // caracteres
    
    @Autowired
    private ResponseContext responseContext;
    
    @Autowired
    private JokeGenerationStrategy jokeGenerationStrategy;
    
    // Almacena los mensajes por chat para mantener el contexto
    private final Map<String, List<String>> chatMessages = new HashMap<>();
    // Contador de mensajes por chat
    private final Map<String, Integer> messageCounters = new HashMap<>();
    
    /**
     * Procesa un nuevo mensaje y determina si debe generar un chiste
     * @param chatId el ID del chat
     * @param message el mensaje recibido
     * @param userName el nombre del usuario que envió el mensaje
     * @return un chiste si corresponde, null si no debe generar chiste
     */
    public String processMessage(String chatId, String message, String userName) {
        if (chatId == null || message == null || message.trim().isEmpty()) {
            return null;
        }
        
        // Agregar mensaje al contexto del chat
        addMessageToContext(chatId, message, userName);
        
        // Incrementar contador de mensajes
        int messageCount = messageCounters.getOrDefault(chatId, 0) + 1;
        messageCounters.put(chatId, messageCount);
        
        // Verificar si es momento de generar un chiste
        if (shouldGenerateJoke(messageCount)) {
            try {
                String context = buildConversationContext(chatId);
                
                // Verificar si el contexto es apropiado para un chiste
                if (jokeGenerationStrategy.isAppropriateForJoke(context)) {
                    String joke = responseContext.executeStrategy(
                        ResponseContext.StrategyType.JOKE_GENERATION, context);
                    
                    // Reiniciar contador después de generar chiste
                    messageCounters.put(chatId, 0);
                    
                    logger.info("Chiste generado para chat {}", chatId);
                    return joke;
                } else {
                    logger.info("Contexto no apropiado para chiste en chat {}", chatId);
                }
            } catch (AIServiceException e) {
                logger.error("Error generando chiste para chat {}: {}", chatId, e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Determina si debe generar un chiste basado en el número de mensajes
     */
    private boolean shouldGenerateJoke(int messageCount) {
        return messageCount >= MIN_MESSAGES_FOR_JOKE && messageCount <= MAX_MESSAGES_FOR_JOKE;
    }
    
    /**
     * Agrega un mensaje al contexto de conversación del chat
     */
    private void addMessageToContext(String chatId, String message, String userName) {
        List<String> messages = chatMessages.computeIfAbsent(chatId, k -> new ArrayList<>());
        
        String formattedMessage = String.format("%s: %s", userName, message);
        messages.add(formattedMessage);
        
        // Mantener solo los últimos mensajes para evitar contextos muy largos
        if (messages.size() > 10) {
            messages.remove(0);
        }
    }
    
    /**
     * Construye el contexto de conversación para generar el chiste
     */
    private String buildConversationContext(String chatId) {
        List<String> messages = chatMessages.get(chatId);
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        for (String message : messages) {
            if (context.length() + message.length() > MAX_CONTEXT_LENGTH) {
                break;
            }
            context.append(message).append("\n");
        }
        
        return context.toString().trim();
    }
    
    /**
     * Limpia el contexto de un chat específico
     */
    public void clearChatContext(String chatId) {
        chatMessages.remove(chatId);
        messageCounters.remove(chatId);
        logger.info("Contexto limpiado para chat {}", chatId);
    }
    
    /**
     * Obtiene estadísticas del bot de chistes
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("chatsActivos", chatMessages.size());
        stats.put("servicioDisponible", responseContext.isStrategyAvailable(
            ResponseContext.StrategyType.JOKE_GENERATION));
        
        int totalMessages = messageCounters.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
        stats.put("mensajesTotales", totalMessages);
        
        return stats;
    }
    
    /**
     * Verifica si el servicio de chistes está disponible
     */
    public boolean isServiceAvailable() {
        return responseContext.isStrategyAvailable(ResponseContext.StrategyType.JOKE_GENERATION);
    }
    
    /**
     * Genera un chiste manual para un chat específico
     */
    public String generateManualJoke(String chatId) {
        try {
            String context = buildConversationContext(chatId);
            if (context.isEmpty()) {
                context = "conversación general";
            }
            
            return responseContext.executeStrategy(
                ResponseContext.StrategyType.JOKE_GENERATION, context);
        } catch (AIServiceException e) {
            logger.error("Error generando chiste manual: {}", e.getMessage());
            return "😅 Lo siento, no puedo generar un chiste en este momento.";
        }
    }
}