# 🤖 Telegram AI Bot - Servidor de Aplicación

Un servidor de aplicación Java que integra Telegram Bot API con servicios de Inteligencia Artificial (Grok/DeepSeek) para proporcionar dos funcionalidades principales:

1. **Bot de Chistes Contextual**: Genera chistes automáticamente cada 3-4 mensajes en grupos de Telegram basándose en el contexto de la conversación.
2. **Asesor de Ventas IA**: Analiza conversaciones cliente-vendedor y proporciona consejos especializados para diferentes objetivos de venta.

## 🏗️ Patrones de Diseño Implementados

### 1. **Singleton** 🔀
- **Clase**: `ConfigurationManager`
- **Propósito**: Garantiza una única instancia para manejar la configuración global de la aplicación
- **Implementación**: Thread-safe con Spring Framework, maneja todas las configuraciones de APIs y tokens

### 2. **Adapter** 🔌
- **Clases**: `TelegramBotAdapter`, `GrokAIAdapter`
- **Propósito**: Desacopla la lógica interna de las APIs externas específicas
- **Implementación**: 
  - `TelegramBotAdapter`: Adapta la API de Telegram para usar nuestra interfaz unificada
  - `GrokAIAdapter`: Adapta la API de Grok/X.AI para servicios de IA

### 3. **Strategy** 🎯
- **Clases**: `JokeGenerationStrategy`, `RefundRejectionStrategy`, `UpsellingStrategy`, `PurchaseMotivationStrategy`
- **Propósito**: Permite cambiar dinámicamente la lógica de respuesta según el objetivo específico
- **Implementación**: Contexto que selecciona la estrategia apropiada basada en el tipo de interacción

## 🚀 Funcionalidades Principales

### Bot de Chistes Inteligente
- Analiza conversaciones grupales en tiempo real
- Genera chistes contextualmente relevantes cada 3-4 mensajes
- Filtra contenido sensible para mantener un ambiente apropiado
- Comando manual `/chiste` para generar chistes bajo demanda

### Asesor de Ventas IA
- **Rechazo de Devoluciones**: Estrategias diplomáticas para manejar solicitudes de devolución
- **Upselling**: Técnicas para sugerir productos superiores o adicionales
- **Motivación de Compra**: Métodos para incentivar la decisión de compra
- **Análisis General**: Evaluación completa de conversaciones de venta

## 🛠️ Tecnologías Utilizadas

- **Java 17**: Lenguaje de programación principal
- **Spring Boot 3.2**: Framework de aplicación
- **Telegram Bot API**: Integración con Telegram
- **Grok AI / DeepSeek**: Servicios de Inteligencia Artificial
- **Maven**: Gestión de dependencias y construcción
- **Docker**: Containerización
- **Apache HTTP Client**: Comunicación con APIs REST

## 📋 Prerrequisitos

1. **Java 17** o superior
2. **Maven 3.6** o superior
3. **Docker** (opcional, para containerización)
4. **Token de Bot de Telegram** (obtener de @BotFather)
5. **API Key de Grok** (registrarse en X.AI)

## ⚙️ Configuración

### 1. Crear Bot de Telegram
```bash
# Enviar mensaje a @BotFather en Telegram
/newbot
# Seguir instrucciones y guardar el token
```

### 2. Obtener API Key de Grok
```bash
# Visitar https://x.ai/
# Registrarse y obtener API key
```

### 3. Configurar Variables de Entorno
```bash
# Copiar archivo de ejemplo
cp .env.example .env

# Editar .env con tus valores
TELEGRAM_BOT_TOKEN=tu_token_aqui
TELEGRAM_BOT_USERNAME=tu_username_aqui
AI_API_KEY=tu_api_key_aqui
```

## 🚀 Instalación y Ejecución

### Opción 1: Ejecución Local
```bash
# Clonar repositorio
git clone https://github.com/JorgeAndresChp/TelegramBotAPI.git
cd TelegramBotAPI

# Configurar variables de entorno
export TELEGRAM_BOT_TOKEN="tu_token"
export TELEGRAM_BOT_USERNAME="tu_username"
export AI_API_KEY="tu_api_key"

# Compilar y ejecutar
mvn clean package
java -jar target/telegram-ai-bot-1.0.0.jar
```

### Opción 2: Docker
```bash
# Compilar imagen
docker build -t telegram-ai-bot .

# Ejecutar con Docker Compose
docker-compose up -d

# Ver logs
docker-compose logs -f telegram-bot
```

### Opción 3: Docker con Monitoreo
```bash
# Ejecutar con Prometheus para monitoreo
docker-compose --profile with-monitoring up -d
```

## 📱 Comandos del Bot

### Comandos Generales
- `/start` - Inicializar bot y mostrar bienvenida
- `/help` - Mostrar todos los comandos disponibles
- `/estado` - Ver estado del bot y servicios
- `/chiste` - Generar chiste manual

### Comandos de Asesoría de Ventas
- `/rechazar_devolucion [conversación]` - Consejos para rechazar devoluciones
- `/upselling [conversación]` - Estrategias de upselling
- `/motivar_compra [conversación]` - Técnicas de motivación
- `/analisis_general [conversación]` - Análisis completo
- `/ayuda_ventas` - Ayuda detallada para asesoría

### Comandos de Administración
- `/limpiar_contexto` - Reiniciar historial de mensajes

## 🔧 API REST para Administración

El servidor expone endpoints REST para monitoreo y administración:

```bash
# Health check
GET /api/bot/health

# Estadísticas generales
GET /api/bot/stats

# Información de patrones de diseño
GET /api/bot/patterns

# Información de la aplicación
GET /api/bot/info

# Limpiar contexto de chat específico
DELETE /api/bot/chat/{chatId}/context
```

## 📊 Ejemplos de Uso

### Ejemplo 1: Bot de Chistes en Grupo
```
Usuario1: ¿Cómo estuvo tu día en el trabajo?
Usuario2: Horrible, mi jefe me pidió trabajar hasta tarde
Usuario3: Al menos tienes trabajo, yo sigo buscando
Bot: 😄 ¿Saben cuál es la diferencia entre un jefe y un mosquito? 
     ¡Que al mosquito puedes aplastarlo! 🦟
```

### Ejemplo 2: Asesoría de Ventas
```
Comando: /upselling

Conversación:
Cliente: Me gusta este teléfono básico de $200
Vendedor: Es un buen modelo, ¿para qué lo vas a usar principalmente?
Cliente: Solo para llamadas y WhatsApp
Vendedor: Perfecto para eso

Respuesta del Bot:
📈 ESTRATEGIA: Upselling
⏰ 22/09/2025 14:30

ANÁLISIS DE LA SITUACIÓN:
El cliente muestra interés en un producto básico pero no has explorado 
sus necesidades adicionales...

ESTRATEGIAS RECOMENDADAS:
1. Pregunta sobre fotografía: "¿Te gusta tomar fotos?"
2. Menciona la duración de batería del modelo superior
3. Destaca la diferencia en velocidad para apps

FRASES ESPECÍFICAS:
- "Por solo $50 más, tienes una cámara que te sorprenderá"
- "Este modelo premium tiene batería para 2 días completos"
```

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/telegrambotapi/
│   │   ├── adapter/           # Patrón Adapter
│   │   │   ├── AIServiceAdapter.java
│   │   │   ├── GrokAIAdapter.java
│   │   │   └── TelegramBotAdapter.java
│   │   ├── config/            # Patrón Singleton
│   │   │   ├── ConfigurationManager.java
│   │   │   └── SpringConfiguration.java
│   │   ├── strategy/          # Patrón Strategy
│   │   │   ├── ResponseStrategy.java
│   │   │   ├── JokeGenerationStrategy.java
│   │   │   ├── RefundRejectionStrategy.java
│   │   │   ├── UpsellingStrategy.java
│   │   │   ├── PurchaseMotivationStrategy.java
│   │   │   └── ResponseContext.java
│   │   ├── service/           # Lógica de negocio
│   │   │   ├── JokeBotService.java
│   │   │   ├── SalesAdvisorService.java
│   │   │   └── TelegramBotService.java
│   │   ├── controller/        # API REST
│   │   │   └── BotController.java
│   │   └── TelegramBotApplication.java
│   └── resources/
│       └── application.properties
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## 🔒 Seguridad

- ✅ Validación de entrada para prevenir inyecciones
- ✅ Rate limiting implementado
- ✅ Filtrado de contenido sensible
- ✅ Logs de auditoría para todas las interacciones
- ✅ Variables de entorno para configuración sensible
- ✅ Usuario no-root en contenedor Docker

## 📈 Monitoreo y Logs

### Logs Disponibles
- Logs de aplicación: `/app/logs/`
- Logs de interacciones: Nivel INFO
- Logs de errores: Nivel ERROR con stack traces

### Métricas
- Estadísticas de chistes generados
- Contador de consejos de venta por tipo
- Health checks automáticos
- Monitoreo de APIs externas

## 🧪 Testing

```bash
# Ejecutar tests unitarios
mvn test

# Ejecutar tests de integración
mvn verify

# Generar reporte de cobertura
mvn jacoco:report
```

## 🚨 Troubleshooting

### Problemas Comunes

1. **Bot no responde**
   ```bash
   # Verificar configuración
   curl http://localhost:8080/api/bot/health
   ```

2. **Error de API de IA**
   ```bash
   # Verificar API key y límites
   # Revisar logs: docker-compose logs telegram-bot
   ```

3. **Problema de permisos en Docker**
   ```bash
   # Reconstruir imagen
   docker-compose down
   docker-compose build --no-cache
   docker-compose up -d
   ```

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama para feature (`git checkout -b feature/NuevaFuncionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/NuevaFuncionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 👥 Autores

- **Jorge Andrés** - Desarrollo principal
- **Equipo de desarrollo** - Contribuciones adicionales

## 🙏 Agradecimientos

- Telegram Bot API por la excelente documentación
- X.AI por proporcionar acceso a Grok
- Spring Boot community por el framework robusto
- Patrones de diseño Gang of Four

---

## 📞 Soporte

Para preguntas o soporte técnico:
- 📧 Email: soporte@telegrambotapi.com
- 💬 Telegram: @TelegramBotAPISupport
- 🐛 Issues: [GitHub Issues](https://github.com/JorgeAndresChp/TelegramBotAPI/issues)

---

**¡Esperamos que disfrutes usando el Telegram AI Bot!** 🎉