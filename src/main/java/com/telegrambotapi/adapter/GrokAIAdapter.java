package com.telegrambotapi.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegrambotapi.config.ConfigurationManager;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Implementación del patrón Adapter para la API de Grok/X.AI
 * Adapta la API específica de Grok para que sea compatible con nuestra interfaz AIServiceAdapter
 */
@Component
public class GrokAIAdapter implements AIServiceAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(GrokAIAdapter.class);
    private final ObjectMapper objectMapper;
    private final ConfigurationManager config;
    
    public GrokAIAdapter() {
        this.objectMapper = new ObjectMapper();
        this.config = ConfigurationManager.getInstance();
    }
    
    @Override
    public String generateResponse(String prompt) throws AIServiceException {
        try {
            String requestBody = buildRequestBody(prompt);
            return callAIAPI(requestBody);
        } catch (Exception e) {
            logger.error("Error generando respuesta de IA", e);
            throw new AIServiceException("Error al generar respuesta de IA: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateJoke(String conversationContext) throws AIServiceException {
        String jokePrompt = String.format(
            "Basándote en el siguiente contexto de conversación, genera un chiste apropiado y divertido " +
            "que sea relevante al tema discutido. El chiste debe ser respetuoso y adecuado para un entorno de grupo. " +
            "Contexto: %s\n\nGenera solo el chiste, sin explicaciones adicionales.", 
            conversationContext
        );
        return generateResponse(jokePrompt);
    }
    
    @Override
    public String analyzeSalesConversation(String conversation, String objective) throws AIServiceException {
        String salesPrompt = String.format(
            "Eres un experto consultor de ventas. Analiza la siguiente conversación entre un cliente y un vendedor, " +
            "y proporciona consejos específicos para lograr el objetivo: %s.\n\n" +
            "Conversación:\n%s\n\n" +
            "Proporciona consejos concretos y actionables para el vendedor, incluyendo:\n" +
            "1. Análisis de la situación actual\n" +
            "2. Estrategias recomendadas\n" +
            "3. Frases o argumentos específicos que puede usar\n" +
            "4. Qué evitar en esta situación",
            objective, conversation
        );
        return generateResponse(salesPrompt);
    }
    
    @Override
    public boolean isServiceAvailable() {
        try {
            String testPrompt = "Di 'OK' si puedes responder";
            String response = generateResponse(testPrompt);
            return response != null && !response.trim().isEmpty();
        } catch (Exception e) {
            logger.warn("Servicio de IA no disponible", e);
            return false;
        }
    }
    
    /**
     * Construye el cuerpo de la petición HTTP para la API de Grok
     */
    private String buildRequestBody(String prompt) throws JsonProcessingException {
        return String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":1000,\"temperature\":0.7}",
            config.getAiModel(),
            prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );
    }
    
    /**
     * Realiza la llamada HTTP a la API de IA
     */
    private String callAIAPI(String requestBody) throws AIServiceException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(config.getAiApiUrl());
            
            // Headers
            httpPost.setHeader("Authorization", "Bearer " + config.getAiApiKey());
            httpPost.setHeader("Content-Type", "application/json");
            
            // Body
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
            
            @SuppressWarnings("deprecation")
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                int statusCode = response.getCode();
                String responseBody = new String(
                    response.getEntity().getContent().readAllBytes(), 
                    StandardCharsets.UTF_8
                );
                
                if (statusCode == 200) {
                    return extractResponseContent(responseBody);
                } else {
                    logger.error("Error en API de IA. Status: {}, Response: {}", statusCode, responseBody);
                    throw new AIServiceException("Error en API de IA. Status: " + statusCode);
                }
            } finally {
                response.close();
            }
        } catch (IOException e) {
            logger.error("Error de comunicación con API de IA", e);
            throw new AIServiceException("Error de comunicación con API de IA", e);
        }
    }
    
    /**
     * Extrae el contenido de la respuesta de la API de IA
     */
    private String extractResponseContent(String responseBody) throws AIServiceException {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.get("choices");
            
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null) {
                        return content.asText().trim();
                    }
                }
            }
            
            logger.error("Formato de respuesta inesperado: {}", responseBody);
            throw new AIServiceException("Formato de respuesta inesperado de la API de IA");
            
        } catch (JsonProcessingException e) {
            logger.error("Error al parsear respuesta JSON", e);
            throw new AIServiceException("Error al parsear respuesta de IA", e);
        }
    }
}