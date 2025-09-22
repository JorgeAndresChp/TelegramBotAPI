package com.telegrambotapi.adapter;

/**
 * Interfaz del patrón Adapter para servicios de IA.
 * Permite desacoplar la lógica interna de la implementación específica
 * de cada proveedor de IA (Grok, DeepSeek, etc.)
 */
public interface AIServiceAdapter {
    
    /**
     * Genera una respuesta de IA basada en el prompt proporcionado
     * @param prompt el texto de entrada para la IA
     * @return la respuesta generada por la IA
     * @throws AIServiceException si ocurre un error en la comunicación con la IA
     */
    String generateResponse(String prompt) throws AIServiceException;
    
    /**
     * Genera un chiste basado en el contexto de la conversación
     * @param conversationContext el contexto de la conversación
     * @return un chiste apropiado para el contexto
     * @throws AIServiceException si ocurre un error en la comunicación con la IA
     */
    String generateJoke(String conversationContext) throws AIServiceException;
    
    /**
     * Analiza una conversación de ventas y proporciona consejos
     * @param conversation la conversación entre cliente y vendedor
     * @param objective el objetivo específico (rechazar devolución, upselling, etc.)
     * @return consejos para el vendedor
     * @throws AIServiceException si ocurre un error en la comunicación con la IA
     */
    String analyzeSalesConversation(String conversation, String objective) throws AIServiceException;
    
    /**
     * Verifica si el servicio de IA está disponible
     * @return true si el servicio está disponible, false en caso contrario
     */
    boolean isServiceAvailable();
}