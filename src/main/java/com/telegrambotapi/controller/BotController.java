package com.telegrambotapi.controller;

import com.telegrambotapi.config.ConfigurationManager;
import com.telegrambotapi.service.JokeBotService;
import com.telegrambotapi.service.SalesAdvisorService;
import com.telegrambotapi.service.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para administración y monitoreo del bot
 */
@RestController
@RequestMapping("/api/bot")
public class BotController {
    
    @Autowired
    private ConfigurationManager configurationManager;
    
    @Autowired
    private TelegramBotService telegramBotService;
    
    @Autowired
    private JokeBotService jokeBotService;
    
    @Autowired
    private SalesAdvisorService salesAdvisorService;
    
    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        health.put("bot", configurationManager.getTelegramBotUsername());
        health.put("services", Map.of(
            "jokes", jokeBotService.isServiceAvailable(),
            "sales", salesAdvisorService.isServiceAvailable()
        ));
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Obtiene estadísticas generales del bot
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("general", telegramBotService.getServiceStatistics());
        stats.put("jokes", jokeBotService.getStatistics());
        stats.put("sales", salesAdvisorService.getStatistics());
        stats.put("config", Map.of(
            "botUsername", configurationManager.getTelegramBotUsername(),
            "aiModel", configurationManager.getAiModel(),
            "configValid", configurationManager.isConfigurationValid()
        ));
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Limpia el contexto de un chat específico
     */
    @DeleteMapping("/chat/{chatId}/context")
    public ResponseEntity<Map<String, String>> clearChatContext(@PathVariable String chatId) {
        jokeBotService.clearChatContext(chatId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contexto limpiado para chat: " + chatId);
        response.put("timestamp", LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Información sobre los patrones de diseño implementados
     */
    @GetMapping("/patterns")
    public ResponseEntity<Map<String, Object>> getDesignPatterns() {
        Map<String, Object> patterns = new HashMap<>();
        
        patterns.put("singleton", Map.of(
            "class", "ConfigurationManager",
            "description", "Maneja la configuración global de la aplicación",
            "implementation", "Patrón Singleton thread-safe con Spring"
        ));
        
        patterns.put("adapter", Map.of(
            "classes", new String[]{"TelegramBotAdapter", "GrokAIAdapter"},
            "description", "Adapta APIs externas para desacoplar servicios",
            "implementation", "Interfaces que encapsulan Telegram API y AI API"
        ));
        
        patterns.put("strategy", Map.of(
            "classes", new String[]{"JokeGenerationStrategy", "RefundRejectionStrategy", 
                                   "UpsellingStrategy", "PurchaseMotivationStrategy"},
            "description", "Cambia dinámicamente la lógica según el objetivo",
            "implementation", "Contexto que selecciona estrategias de respuesta"
        ));
        
        return ResponseEntity.ok(patterns);
    }
    
    /**
     * Endpoint para información de la aplicación
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApplicationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Telegram AI Bot");
        info.put("version", "1.0.0");
        info.put("description", "Servidor de aplicación con Telegram Bot API e integración con IA");
        info.put("features", new String[]{
            "Generación automática de chistes en grupos",
            "Asesoría de ventas con IA",
            "Análisis de conversaciones cliente-vendedor",
            "Estrategias de upselling y motivación de compra"
        });
        info.put("designPatterns", new String[]{"Singleton", "Adapter", "Strategy"});
        info.put("technologies", new String[]{"Spring Boot", "Telegram Bot API", "Grok AI", "Maven", "Docker"});
        
        return ResponseEntity.ok(info);
    }
}