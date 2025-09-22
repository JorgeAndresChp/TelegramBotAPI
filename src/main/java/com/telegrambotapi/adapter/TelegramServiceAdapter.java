package com.telegrambotapi.adapter;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Interfaz del patrón Adapter para el servicio de Telegram.
 * Permite desacoplar la lógica interna de los detalles específicos de la API de Telegram
 */
public interface TelegramServiceAdapter {
    
    /**
     * Envía un mensaje de texto a un chat específico
     * @param chatId el ID del chat donde enviar el mensaje
     * @param text el texto del mensaje a enviar
     * @return true si el mensaje se envió correctamente, false en caso contrario
     */
    boolean sendTextMessage(String chatId, String text);
    
    /**
     * Extrae el texto del mensaje de un update de Telegram
     * @param update el update recibido de Telegram
     * @return el texto del mensaje, o null si no hay mensaje de texto
     */
    String extractMessageText(Update update);
    
    /**
     * Extrae el ID del chat de un update de Telegram
     * @param update el update recibido de Telegram
     * @return el ID del chat, o null si no se puede extraer
     */
    String extractChatId(Update update);
    
    /**
     * Extrae el ID del usuario de un update de Telegram
     * @param update el update recibido de Telegram
     * @return el ID del usuario, o null si no se puede extraer
     */
    String extractUserId(Update update);
    
    /**
     * Verifica si un update contiene un mensaje de texto
     * @param update el update a verificar
     * @return true si contiene un mensaje de texto, false en caso contrario
     */
    boolean hasTextMessage(Update update);
    
    /**
     * Extrae información del mensaje de un update
     * @param update el update recibido de Telegram
     * @return el objeto Message si existe, null en caso contrario
     */
    Message extractMessage(Update update);
}