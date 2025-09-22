package com.telegrambotapi.service;

import com.telegrambotapi.adapter.TelegramBotAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio principal que coordina todos los componentes del bot y maneja
 * la lógica de procesamiento de mensajes de Telegram.
 */
@Service
public class TelegramBotService {
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);
    
    @Autowired
    private TelegramBotAdapter telegramAdapter;
    
    @Autowired
    private JokeBotService jokeBotService;
    
    @Autowired
    private SalesAdvisorService salesAdvisorService;
    
    // Estados del servicio
    private final Map<String, String> chatStates = new HashMap<>();
    private final Map<String, LocalDateTime> lastActivity = new HashMap<>();
    
    // Constantes
    private static final String STATE_NORMAL = "NORMAL";
    private static final String STATE_WAITING_CONVERSATION = "WAITING_CONVERSATION";
    private static final String STATE_REFUND_REJECTION = "REFUND_REJECTION";
    private static final String STATE_UPSELLING = "UPSELLING";
    private static final String STATE_PURCHASE_MOTIVATION = "PURCHASE_MOTIVATION";
    
    /**
     * Procesa un update recibido de Telegram
     */
    public void processUpdate(Update update) {
        try {
            if (!telegramAdapter.hasTextMessage(update)) {
                return;
            }
            
            String chatId = telegramAdapter.extractChatId(update);
            String messageText = telegramAdapter.extractMessageText(update);
            String userInfo = telegramAdapter.getUserInfo(update);
            
            if (chatId == null || messageText == null) {
                return;
            }
            
            // Actualizar última actividad
            lastActivity.put(chatId, LocalDateTime.now());
            
            logger.info("Procesando mensaje de {}: {}", 
                       userInfo, messageText.substring(0, Math.min(50, messageText.length())));
            
            // Procesar comando o mensaje normal
            if (messageText.startsWith("/")) {
                processCommand(chatId, messageText, userInfo);
            } else {
                processNormalMessage(chatId, messageText, userInfo);
            }
            
        } catch (Exception e) {
            logger.error("Error procesando update: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Procesa comandos del bot
     */
    private void processCommand(String chatId, String command, String userInfo) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : "";
        
        switch (cmd) {
            case "/start":
                handleStartCommand(chatId);
                break;
            case "/help":
                handleHelpCommand(chatId);
                break;
            case "/chiste":
                handleJokeCommand(chatId);
                break;
            case "/rechazar_devolucion":
                handleRefundRejectionCommand(chatId, argument);
                break;
            case "/upselling":
                handleUpsellingCommand(chatId, argument);
                break;
            case "/motivar_compra":
                handlePurchaseMotivationCommand(chatId, argument);
                break;
            case "/analisis_general":
                handleGeneralAnalysisCommand(chatId, argument);
                break;
            case "/ayuda_ventas":
                handleSalesHelpCommand(chatId);
                break;
            case "/estado":
                handleStatusCommand(chatId);
                break;
            case "/limpiar_contexto":
                handleClearContextCommand(chatId);
                break;
            default:
                handleUnknownCommand(chatId, cmd);
        }
    }
    
    /**
     * Procesa mensajes normales (no comandos)
     */
    private void processNormalMessage(String chatId, String messageText, String userInfo) {
        String currentState = chatStates.getOrDefault(chatId, STATE_NORMAL);
        
        switch (currentState) {
            case STATE_REFUND_REJECTION:
                processConversationForRefundRejection(chatId, messageText);
                break;
            case STATE_UPSELLING:
                processConversationForUpselling(chatId, messageText);
                break;
            case STATE_PURCHASE_MOTIVATION:
                processConversationForPurchaseMotivation(chatId, messageText);
                break;
            case STATE_NORMAL:
            default:
                processNormalGroupMessage(chatId, messageText, userInfo);
        }
    }
    
    /**
     * Maneja el comando /start
     */
    private void handleStartCommand(String chatId) {
        String welcomeMessage = 
            "🤖 ¡Hola! Soy tu Asistente de IA\n\n" +
            "🎭 En grupos: Genero chistes basados en la conversación cada 3-4 mensajes\n" +
            "💼 En chats privados: Asesoro ventas analizando conversaciones\n\n" +
            "📋 Comandos disponibles:\n" +
            "• /help - Ver todos los comandos\n" +
            "• /chiste - Generar chiste manual\n" +
            "• /ayuda_ventas - Ayuda para asesoría de ventas\n" +
            "• /estado - Ver estado del bot\n\n" +
            "✨ ¡Empecemos!";
        
        telegramAdapter.sendTextMessage(chatId, welcomeMessage);
        chatStates.put(chatId, STATE_NORMAL);
    }
    
    /**
     * Maneja el comando /help
     */
    private void handleHelpCommand(String chatId) {
        String helpMessage = 
            "🆘 AYUDA - Comandos disponibles:\n\n" +
            "🎭 CHISTES:\n" +
            "• /chiste - Generar chiste manual\n" +
            "• /limpiar_contexto - Limpiar historial de mensajes\n\n" +
            "💼 ASESORÍA DE VENTAS:\n" +
            "• /rechazar_devolucion - Consejos para rechazar devoluciones\n" +
            "• /upselling - Estrategias de upselling\n" +
            "• /motivar_compra - Técnicas de motivación\n" +
            "• /analisis_general - Análisis general de conversación\n" +
            "• /ayuda_ventas - Ayuda detallada de ventas\n\n" +
            "🔧 UTILIDADES:\n" +
            "• /estado - Estado del bot y servicios\n" +
            "• /help - Esta ayuda\n\n" +
            "💡 Tip: En grupos genero chistes automáticamente. " +
            "En chats privados uso los comandos de ventas.";
        
        telegramAdapter.sendTextMessage(chatId, helpMessage);
    }
    
    /**
     * Maneja comando de chiste manual
     */
    private void handleJokeCommand(String chatId) {
        if (jokeBotService.isServiceAvailable()) {
            String joke = jokeBotService.generateManualJoke(chatId);
            telegramAdapter.sendTextMessage(chatId, joke);
        } else {
            telegramAdapter.sendTextMessage(chatId, 
                "😅 El servicio de chistes no está disponible en este momento.");
        }
    }
    
    /**
     * Maneja comando de rechazo de devolución
     */
    private void handleRefundRejectionCommand(String chatId, String conversation) {
        if (conversation.trim().isEmpty()) {
            telegramAdapter.sendTextMessage(chatId, 
                "📝 Envía la conversación cliente-vendedor después del comando.\n" +
                "Ejemplo: /rechazar_devolucion Cliente: Quiero devolver... Vendedor: ...");
            chatStates.put(chatId, STATE_REFUND_REJECTION);
        } else {
            processConversationForRefundRejection(chatId, conversation);
        }
    }
    
    /**
     * Procesa conversación para rechazo de devolución
     */
    private void processConversationForRefundRejection(String chatId, String conversation) {
        if (salesAdvisorService.isValidConversation(conversation)) {
            String advice = salesAdvisorService.analyzeForRefundRejection(conversation, chatId);
            telegramAdapter.sendTextMessage(chatId, advice);
        } else {
            telegramAdapter.sendTextMessage(chatId, 
                "❌ Conversación inválida. Debe tener entre 50 y 10,000 caracteres.");
        }
        chatStates.put(chatId, STATE_NORMAL);
    }
    
    /**
     * Maneja comando de upselling
     */
    private void handleUpsellingCommand(String chatId, String conversation) {
        if (conversation.trim().isEmpty()) {
            telegramAdapter.sendTextMessage(chatId, 
                "📝 Envía la conversación cliente-vendedor después del comando.");
            chatStates.put(chatId, STATE_UPSELLING);
        } else {
            processConversationForUpselling(chatId, conversation);
        }
    }
    
    /**
     * Procesa conversación para upselling
     */
    private void processConversationForUpselling(String chatId, String conversation) {
        if (salesAdvisorService.isValidConversation(conversation)) {
            String advice = salesAdvisorService.analyzeForUpselling(conversation, chatId);
            telegramAdapter.sendTextMessage(chatId, advice);
        } else {
            telegramAdapter.sendTextMessage(chatId, 
                "❌ Conversación inválida. Debe tener entre 50 y 10,000 caracteres.");
        }
        chatStates.put(chatId, STATE_NORMAL);
    }
    
    /**
     * Maneja comando de motivación de compra
     */
    private void handlePurchaseMotivationCommand(String chatId, String conversation) {
        if (conversation.trim().isEmpty()) {
            telegramAdapter.sendTextMessage(chatId, 
                "📝 Envía la conversación cliente-vendedor después del comando.");
            chatStates.put(chatId, STATE_PURCHASE_MOTIVATION);
        } else {
            processConversationForPurchaseMotivation(chatId, conversation);
        }
    }
    
    /**
     * Procesa conversación para motivación de compra
     */
    private void processConversationForPurchaseMotivation(String chatId, String conversation) {
        if (salesAdvisorService.isValidConversation(conversation)) {
            String advice = salesAdvisorService.analyzeForPurchaseMotivation(conversation, chatId);
            telegramAdapter.sendTextMessage(chatId, advice);
        } else {
            telegramAdapter.sendTextMessage(chatId, 
                "❌ Conversación inválida. Debe tener entre 50 y 10,000 caracteres.");
        }
        chatStates.put(chatId, STATE_NORMAL);
    }
    
    /**
     * Maneja comando de análisis general
     */
    private void handleGeneralAnalysisCommand(String chatId, String conversation) {
        if (conversation.trim().isEmpty()) {
            telegramAdapter.sendTextMessage(chatId, 
                "📝 Envía la conversación para analizar después del comando.");
        } else if (salesAdvisorService.isValidConversation(conversation)) {
            String analysis = salesAdvisorService.provideGeneralAnalysis(conversation, chatId);
            telegramAdapter.sendTextMessage(chatId, analysis);
        } else {
            telegramAdapter.sendTextMessage(chatId, 
                "❌ Conversación inválida. Debe tener entre 50 y 10,000 caracteres.");
        }
    }
    
    /**
     * Maneja comando de ayuda de ventas
     */
    private void handleSalesHelpCommand(String chatId) {
        String help = salesAdvisorService.getUsageHelp();
        telegramAdapter.sendTextMessage(chatId, help);
    }
    
    /**
     * Maneja comando de estado
     */
    private void handleStatusCommand(String chatId) {
        Map<String, Object> jokeStats = jokeBotService.getStatistics();
        Map<String, Object> salesStats = salesAdvisorService.getStatistics();
        
        String status = String.format(
            "🔧 ESTADO DEL BOT\n\n" +
            "🎭 Servicio de Chistes:\n" +
            "• Estado: %s\n" +
            "• Chats activos: %s\n" +
            "• Mensajes procesados: %s\n\n" +
            "💼 Servicio de Ventas:\n" +
            "• Estado: %s\n" +
            "• Consejos dados: %s\n\n" +
            "⏰ Última actualización: %s",
            jokeBotService.isServiceAvailable() ? "✅ Activo" : "❌ Inactivo",
            jokeStats.get("chatsActivos"),
            jokeStats.get("mensajesTotales"),
            salesAdvisorService.isServiceAvailable() ? "✅ Activo" : "❌ Inactivo",
            salesStats.get("totalConsejos"),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
        
        telegramAdapter.sendTextMessage(chatId, status);
    }
    
    /**
     * Maneja comando de limpiar contexto
     */
    private void handleClearContextCommand(String chatId) {
        jokeBotService.clearChatContext(chatId);
        chatStates.put(chatId, STATE_NORMAL);
        telegramAdapter.sendTextMessage(chatId, 
            "🧹 Contexto limpiado. El historial de mensajes se ha reiniciado.");
    }
    
    /**
     * Maneja comandos desconocidos
     */
    private void handleUnknownCommand(String chatId, String command) {
        telegramAdapter.sendTextMessage(chatId, 
            "❓ Comando desconocido: " + command + "\n" +
            "Usa /help para ver los comandos disponibles.");
    }
    
    /**
     * Procesa mensajes normales en grupos (para chistes automáticos)
     */
    private void processNormalGroupMessage(String chatId, String messageText, String userInfo) {
        // Solo procesar chistes automáticos en grupos
        if (telegramAdapter.isGroupMessage(createUpdateFromInfo(chatId, messageText))) {
            String joke = jokeBotService.processMessage(chatId, messageText, userInfo);
            if (joke != null) {
                telegramAdapter.sendTextMessage(chatId, joke);
            }
        }
    }
    
    /**
     * Crea un update simulado para verificaciones
     */
    private Update createUpdateFromInfo(String chatId, String messageText) {
        // Implementación simplificada para verificar si es grupo
        // En una implementación real, se mantendría el Update original
        return new Update();
    }
    
    /**
     * Obtiene estadísticas generales del servicio
     */
    public Map<String, Object> getServiceStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("chatsActivos", chatStates.size());
        stats.put("estadosActivos", chatStates);
        stats.put("servicioChistes", jokeBotService.getStatistics());
        stats.put("servicioVentas", salesAdvisorService.getStatistics());
        stats.put("ultimaActividad", lastActivity);
        
        return stats;
    }
}