package com.telegrambotapi.adapter;

import com.telegrambotapi.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Implementación del patrón Adapter para la API de Telegram.
 * Adapta la API específica de Telegram para que sea compatible con nuestra interfaz TelegramServiceAdapter
 */
@Component
public class TelegramBotAdapter extends TelegramLongPollingBot implements TelegramServiceAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotAdapter.class);
    private final ConfigurationManager config;
    
    public TelegramBotAdapter() {
        super(ConfigurationManager.getInstance().getTelegramBotToken());
        this.config = ConfigurationManager.getInstance();
    }
    
    @Override
    public String getBotToken() {
        return config.getTelegramBotToken();
    }
    
    @Override
    public String getBotUsername() {
        return config.getTelegramBotUsername();
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        // Este método será manejado por el servicio principal
        // Aquí solo registramos la recepción del update
        logger.info("Update recibido: {}", update.getUpdateId());
    }
    
    @Override
    public boolean sendTextMessage(String chatId, String text) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            
            execute(message);
            logger.info("Mensaje enviado al chat {}: {}", chatId, text.substring(0, Math.min(50, text.length())));
            return true;
            
        } catch (TelegramApiException e) {
            logger.error("Error enviando mensaje al chat {}: {}", chatId, e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String extractMessageText(Update update) {
        if (hasTextMessage(update)) {
            return update.getMessage().getText();
        }
        return null;
    }
    
    @Override
    public String extractChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        }
        return null;
    }
    
    @Override
    public String extractUserId(Update update) {
        if (update.hasMessage() && update.getMessage().getFrom() != null) {
            return update.getMessage().getFrom().getId().toString();
        }
        return null;
    }
    
    @Override
    public boolean hasTextMessage(Update update) {
        return update.hasMessage() && 
               update.getMessage().hasText() && 
               update.getMessage().getText() != null && 
               !update.getMessage().getText().trim().isEmpty();
    }
    
    @Override
    public Message extractMessage(Update update) {
        if (update.hasMessage()) {
            return update.getMessage();
        }
        return null;
    }
    
    /**
     * Método de utilidad para obtener información del usuario
     */
    public String getUserInfo(Update update) {
        Message message = extractMessage(update);
        if (message != null && message.getFrom() != null) {
            String firstName = message.getFrom().getFirstName();
            String lastName = message.getFrom().getLastName();
            String username = message.getFrom().getUserName();
            
            StringBuilder userInfo = new StringBuilder(firstName != null ? firstName : "");
            if (lastName != null) {
                userInfo.append(" ").append(lastName);
            }
            if (username != null) {
                userInfo.append(" (@").append(username).append(")");
            }
            
            return userInfo.toString().trim();
        }
        return "Usuario desconocido";
    }
    
    /**
     * Verifica si el mensaje proviene de un grupo
     */
    public boolean isGroupMessage(Update update) {
        Message message = extractMessage(update);
        return message != null && message.getChat().isGroupChat();
    }
    
    /**
     * Obtiene el título del grupo (si aplica)
     */
    public String getGroupTitle(Update update) {
        Message message = extractMessage(update);
        if (message != null && message.getChat().isGroupChat()) {
            return message.getChat().getTitle();
        }
        return null;
    }
}