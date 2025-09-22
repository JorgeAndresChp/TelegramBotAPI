package com.telegrambotapi.service;

import com.telegrambotapi.adapter.AIServiceException;
import com.telegrambotapi.strategy.ResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para proporcionar asesoría de ventas basada en análisis de conversaciones
 * entre clientes y vendedores usando IA.
 */
@Service
public class SalesAdvisorService {
    
    private static final Logger logger = LoggerFactory.getLogger(SalesAdvisorService.class);
    
    @Autowired
    private ResponseContext responseContext;
    
    // Estadísticas del servicio
    private final Map<String, Integer> advisoryCount = new HashMap<>();
    
    /**
     * Analiza una conversación de ventas y proporciona consejos para rechazar una devolución
     * @param conversation la conversación entre cliente y vendedor
     * @param advisorChatId el ID del chat donde se enviará el consejo
     * @return consejo para rechazar la devolución
     */
    public String analyzeForRefundRejection(String conversation, String advisorChatId) {
        logger.info("Analizando conversación para rechazo de devolución");
        
        try {
            String advice = responseContext.executeStrategy(
                ResponseContext.StrategyType.REFUND_REJECTION, conversation);
            
            incrementAdvisoryCount("refund_rejection");
            logAdvice(advisorChatId, "Rechazo de Devolución", conversation);
            
            return formatAdvice("🚫 ESTRATEGIA: Rechazo de Devolución", advice);
            
        } catch (AIServiceException e) {
            logger.error("Error analizando para rechazo de devolución: {}", e.getMessage());
            return "❌ Error: No pude analizar la conversación para rechazo de devolución. " +
                   "Verifica que el servicio de IA esté disponible.";
        }
    }
    
    /**
     * Analiza una conversación de ventas y proporciona consejos para upselling
     * @param conversation la conversación entre cliente y vendedor
     * @param advisorChatId el ID del chat donde se enviará el consejo
     * @return consejo para realizar upselling
     */
    public String analyzeForUpselling(String conversation, String advisorChatId) {
        logger.info("Analizando conversación para upselling");
        
        try {
            String advice = responseContext.executeStrategy(
                ResponseContext.StrategyType.UPSELLING, conversation);
            
            incrementAdvisoryCount("upselling");
            logAdvice(advisorChatId, "Upselling", conversation);
            
            return formatAdvice("📈 ESTRATEGIA: Upselling", advice);
            
        } catch (AIServiceException e) {
            logger.error("Error analizando para upselling: {}", e.getMessage());
            return "❌ Error: No pude analizar la conversación para upselling. " +
                   "Verifica que el servicio de IA esté disponible.";
        }
    }
    
    /**
     * Analiza una conversación de ventas y proporciona consejos para motivar la compra
     * @param conversation la conversación entre cliente y vendedor
     * @param advisorChatId el ID del chat donde se enviará el consejo
     * @return consejo para motivar la compra
     */
    public String analyzeForPurchaseMotivation(String conversation, String advisorChatId) {
        logger.info("Analizando conversación para motivación de compra");
        
        try {
            String advice = responseContext.executeStrategy(
                ResponseContext.StrategyType.PURCHASE_MOTIVATION, conversation);
            
            incrementAdvisoryCount("purchase_motivation");
            logAdvice(advisorChatId, "Motivación de Compra", conversation);
            
            return formatAdvice("💪 ESTRATEGIA: Motivación de Compra", advice);
            
        } catch (AIServiceException e) {
            logger.error("Error analizando para motivación de compra: {}", e.getMessage());
            return "❌ Error: No pude analizar la conversación para motivación de compra. " +
                   "Verifica que el servicio de IA esté disponible.";
        }
    }
    
    /**
     * Proporciona análisis general de una conversación de ventas
     * @param conversation la conversación entre cliente y vendedor
     * @param advisorChatId el ID del chat donde se enviará el análisis
     * @return análisis general con recomendaciones
     */
    public String provideGeneralAnalysis(String conversation, String advisorChatId) {
        logger.info("Proporcionando análisis general de conversación");
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("📊 ANÁLISIS GENERAL DE VENTAS\n\n");
        
        // Análisis básico
        analysis.append("📝 Resumen de la conversación:\n");
        analysis.append("• Longitud: ").append(conversation.length()).append(" caracteres\n");
        analysis.append("• Líneas de diálogo: ").append(conversation.split("\n").length).append("\n\n");
        
        // Sugerencias generales
        analysis.append("💡 Recomendaciones generales:\n");
        analysis.append("• Usa las estrategias específicas para análisis detallado\n");
        analysis.append("• Comandos disponibles:\n");
        analysis.append("  - /rechazar_devolucion - Para rechazar devoluciones\n");
        analysis.append("  - /upselling - Para técnicas de upselling\n");
        analysis.append("  - /motivar_compra - Para motivar la compra\n\n");
        
        analysis.append("⏰ Análisis realizado: ");
        analysis.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        logAdvice(advisorChatId, "Análisis General", conversation);
        
        return analysis.toString();
    }
    
    /**
     * Formatea un consejo con un encabezado y timestamp
     */
    private String formatAdvice(String header, String advice) {
        StringBuilder formatted = new StringBuilder();
        formatted.append(header).append("\n");
        formatted.append("⏰ ").append(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        formatted.append(advice);
        formatted.append("\n\n📋 Consejo generado por IA");
        
        return formatted.toString();
    }
    
    /**
     * Incrementa el contador para un tipo específico de asesoría
     */
    private void incrementAdvisoryCount(String type) {
        advisoryCount.put(type, advisoryCount.getOrDefault(type, 0) + 1);
    }
    
    /**
     * Registra el consejo proporcionado para auditoría
     */
    private void logAdvice(String advisorChatId, String type, String conversation) {
        logger.info("Consejo '{}' proporcionado al chat {}. Conversación de {} caracteres", 
                   type, advisorChatId, conversation.length());
    }
    
    /**
     * Obtiene estadísticas del servicio de asesoría
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConsejos", advisoryCount.values().stream()
            .mapToInt(Integer::intValue).sum());
        stats.put("consejosPorTipo", new HashMap<>(advisoryCount));
        stats.put("serviciosDisponibles", getAvailableServices());
        stats.put("ultimaActualizacion", LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        
        return stats;
    }
    
    /**
     * Verifica qué servicios están disponibles
     */
    private Map<String, Boolean> getAvailableServices() {
        Map<String, Boolean> services = new HashMap<>();
        services.put("rechazoDevolucion", responseContext.isStrategyAvailable(
            ResponseContext.StrategyType.REFUND_REJECTION));
        services.put("upselling", responseContext.isStrategyAvailable(
            ResponseContext.StrategyType.UPSELLING));
        services.put("motivacionCompra", responseContext.isStrategyAvailable(
            ResponseContext.StrategyType.PURCHASE_MOTIVATION));
        
        return services;
    }
    
    /**
     * Verifica si el servicio de asesoría está completamente disponible
     */
    public boolean isServiceAvailable() {
        return responseContext.isStrategyAvailable(ResponseContext.StrategyType.REFUND_REJECTION) &&
               responseContext.isStrategyAvailable(ResponseContext.StrategyType.UPSELLING) &&
               responseContext.isStrategyAvailable(ResponseContext.StrategyType.PURCHASE_MOTIVATION);
    }
    
    /**
     * Valida el formato de una conversación
     */
    public boolean isValidConversation(String conversation) {
        return conversation != null && 
               !conversation.trim().isEmpty() && 
               conversation.length() >= 50 && // mínimo 50 caracteres
               conversation.length() <= 10000; // máximo 10000 caracteres
    }
    
    /**
     * Proporciona ayuda sobre cómo usar el servicio
     */
    public String getUsageHelp() {
        return "🔧 AYUDA - Asesor de Ventas\n\n" +
               "📝 Cómo usar:\n" +
               "1. Copia la conversación entre cliente y vendedor\n" +
               "2. Usa uno de estos comandos seguido de la conversación:\n\n" +
               "🚫 /rechazar_devolucion [conversación]\n" +
               "   - Consejos para rechazar devoluciones diplomáticamente\n\n" +
               "📈 /upselling [conversación]\n" +
               "   - Estrategias para vender productos mejores\n\n" +
               "💪 /motivar_compra [conversación]\n" +
               "   - Técnicas para motivar la compra\n\n" +
               "📊 /analisis_general [conversación]\n" +
               "   - Análisis general con recomendaciones\n\n" +
               "⚠️ Requisitos:\n" +
               "• La conversación debe tener entre 50 y 10,000 caracteres\n" +
               "• Incluye tanto mensajes del cliente como del vendedor\n" +
               "• Usa formato: 'Cliente: mensaje' y 'Vendedor: mensaje'";
    }
}