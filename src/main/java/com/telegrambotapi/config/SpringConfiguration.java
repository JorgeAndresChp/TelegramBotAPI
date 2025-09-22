package com.telegrambotapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring para inicializar el Singleton ConfigurationManager
 */
@Configuration
public class SpringConfiguration {
    
    @Bean
    public ConfigurationManager configurationManager() {
        ConfigurationManager manager = ConfigurationManager.getInstance();
        ConfigurationManager.setInstance(manager);
        return manager;
    }
}