package com.telegrambotapi;

import com.telegrambotapi.adapter.TelegramBotAdapter;
import com.telegrambotapi.config.ConfigurationManager;
import com.telegrambotapi.service.TelegramBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Aplicación principal del servidor de bot de Telegram con IA.
 * Implementa los patrones de diseño Singleton, Adapter y Strategy.
 */
@SpringBootApplication
public class TelegramBotApplication implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotApplication.class);
    
    @Autowired
    private ConfigurationManager configurationManager;
    
    @Autowired
    private TelegramBotService telegramBotService;
    
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 Iniciando Telegram Bot Application");
        
        // Validar configuración
        if (!configurationManager.isConfigurationValid()) {
            logger.error("❌ Configuración inválida. Verifica las variables de entorno:");
            logger.error("TELEGRAM_BOT_TOKEN, TELEGRAM_BOT_USERNAME, AI_API_KEY");
            return;
        }
        
        logger.info("✅ Configuración validada: {}", configurationManager);
        
        try {
            // Registrar el bot con la API de Telegram
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            
            // Crear adapter personalizado que delegue al servicio
            TelegramBotAdapter botAdapter = createBotAdapter();
            botsApi.registerBot(botAdapter);
            
            logger.info("🤖 Bot registrado exitosamente: {}", 
                       configurationManager.getTelegramBotUsername());
            logger.info("🎯 Bot listo para recibir mensajes...");
            
        } catch (TelegramApiException e) {
            logger.error("❌ Error registrando bot: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Crea un adapter personalizado que delegue el procesamiento al servicio
     */
    @Bean
    public TelegramBotAdapter createBotAdapter() {
        return new TelegramBotAdapter() {
            @Override
            public void onUpdateReceived(Update update) {
                try {
                    // Delegar el procesamiento al servicio principal
                    telegramBotService.processUpdate(update);
                } catch (Exception e) {
                    logger.error("Error procesando update en adapter: {}", e.getMessage(), e);
                }
            }
        };
    }
}