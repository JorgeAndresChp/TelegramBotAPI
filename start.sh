#!/bin/bash

# Script de inicio para desarrollo local
echo "🚀 Iniciando Telegram AI Bot - Desarrollo Local"

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado. Instala Java 17 o superior."
    exit 1
fi

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Instala Maven 3.6 o superior."
    exit 1
fi

# Verificar variables de entorno
if [ -z "$TELEGRAM_BOT_TOKEN" ] || [ -z "$AI_API_KEY" ]; then
    echo "❌ Variables de entorno requeridas no configuradas:"
    echo "   TELEGRAM_BOT_TOKEN"
    echo "   TELEGRAM_BOT_USERNAME" 
    echo "   AI_API_KEY"
    echo ""
    echo "💡 Tip: Copia .env.example a .env y configura tus valores"
    echo "        Luego ejecuta: source .env"
    exit 1
fi

echo "✅ Prerrequisitos verificados"
echo ""

# Compilar proyecto
echo "📦 Compilando proyecto..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Error en compilación"
    exit 1
fi

echo "✅ Compilación exitosa"
echo ""

# Ejecutar aplicación
echo "🤖 Iniciando bot..."
java -jar target/telegram-ai-bot-1.0.0.jar