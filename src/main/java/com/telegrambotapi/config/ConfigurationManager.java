package com.telegrambotapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Patrón Singleton para manejar la configuración global de la aplicación.
 * Este patrón garantiza que solo exista una instancia de configuración
 * en toda la aplicación, proporcionando un punto de acceso global
 * a los parámetros de configuración.
 */
@Component
public class ConfigurationManager {
    
    private static ConfigurationManager instance;
    
    @Value("${telegram.bot.token}")
    private String telegramBotToken;
    
    @Value("${telegram.bot.username}")
    private String telegramBotUsername;
    
    @Value("${ai.api.key}")
    private String aiApiKey;
    
    @Value("${ai.api.url}")
    private String aiApiUrl;
    
    @Value("${ai.model}")
    private String aiModel;
    
    /**
     * Constructor privado para evitar la instanciación directa
     */
    private ConfigurationManager() {
        // Constructor privado para implementar Singleton
    }
    
    /**
     * Método estático para obtener la única instancia de ConfigurationManager
     * @return la instancia única de ConfigurationManager
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    
    /**
     * Método para establecer la instancia (usado por Spring)
     */
    public static void setInstance(ConfigurationManager configurationManager) {
        instance = configurationManager;
    }
    
    // Getters para acceder a la configuración
    
    public String getTelegramBotToken() {
        return telegramBotToken;
    }
    
    public String getTelegramBotUsername() {
        return telegramBotUsername;
    }
    
    public String getAiApiKey() {
        return aiApiKey;
    }
    
    public String getAiApiUrl() {
        return aiApiUrl;
    }
    
    public String getAiModel() {
        return aiModel;
    }
    
    // Setters (necesarios para Spring)
    
    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
    }
    
    public void setTelegramBotUsername(String telegramBotUsername) {
        this.telegramBotUsername = telegramBotUsername;
    }
    
    public void setAiApiKey(String aiApiKey) {
        this.aiApiKey = aiApiKey;
    }
    
    public void setAiApiUrl(String aiApiUrl) {
        this.aiApiUrl = aiApiUrl;
    }
    
    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }
    
    /**
     * Valida que todas las configuraciones requeridas estén presentes
     * @return true si la configuración es válida, false en caso contrario
     */
    public boolean isConfigurationValid() {
        return telegramBotToken != null && !telegramBotToken.isEmpty() &&
               telegramBotUsername != null && !telegramBotUsername.isEmpty() &&
               aiApiKey != null && !aiApiKey.isEmpty();
    }
    
    @Override
    public String toString() {
        return "ConfigurationManager{" +
                "telegramBotUsername='" + telegramBotUsername + '\'' +
                ", aiApiUrl='" + aiApiUrl + '\'' +
                ", aiModel='" + aiModel + '\'' +
                ", configurationValid=" + isConfigurationValid() +
                '}';
    }
}