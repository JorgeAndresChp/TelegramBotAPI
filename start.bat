@echo off
REM Script de inicio para Windows
echo 🚀 Iniciando Telegram AI Bot - Desarrollo Local

REM Verificar Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java no está instalado. Instala Java 17 o superior.
    exit /b 1
)

REM Verificar Maven
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven no está instalado. Instala Maven 3.6 o superior.
    exit /b 1
)

REM Verificar variables de entorno
if "%TELEGRAM_BOT_TOKEN%"=="" (
    echo ❌ Variable TELEGRAM_BOT_TOKEN no configurada
    echo 💡 Tip: Configura las variables de entorno requeridas
    exit /b 1
)

if "%AI_API_KEY%"=="" (
    echo ❌ Variable AI_API_KEY no configurada
    echo 💡 Tip: Configura las variables de entorno requeridas
    exit /b 1
)

echo ✅ Prerrequisitos verificados
echo.

REM Compilar proyecto
echo 📦 Compilando proyecto...
mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo ❌ Error en compilación
    exit /b 1
)

echo ✅ Compilación exitosa
echo.

REM Ejecutar aplicación
echo 🤖 Iniciando bot...
java -jar target\telegram-ai-bot-1.0.0.jar