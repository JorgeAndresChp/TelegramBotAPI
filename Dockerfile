# Usar imagen base de OpenJDK 17
FROM openjdk:17-jdk-slim

# Metadata
LABEL maintainer="Telegram Bot API Team"
LABEL description="Servidor de aplicación con Telegram Bot API e integración con IA"
LABEL version="1.0.0"

# Variables de entorno
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080

# Crear directorio de trabajo
WORKDIR /app

# Crear usuario no-root para seguridad
RUN groupadd -r botuser && useradd -r -g botuser botuser

# Copiar archivo de configuración de Maven para cache de dependencias
COPY pom.xml .

# Copiar código fuente
COPY src ./src

# Instalar Maven y construir la aplicación
RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean package -DskipTests && \
    mv target/*.jar app.jar && \
    apt-get remove -y maven && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    rm -rf ~/.m2 && \
    rm -rf src && \
    rm pom.xml

# Cambiar ownership del directorio
RUN chown -R botuser:botuser /app

# Cambiar a usuario no-root
USER botuser

# Exponer puerto
EXPOSE ${SERVER_PORT}

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/api/bot/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]