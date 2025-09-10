# ===== STAGE 1: BUILD =====
FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /app

# copie o pom primeiro para cachear melhor as deps
COPY pom.xml .

# baixa dependências usando o settings montado
RUN --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn -s /root/.m2/settings.xml -B -V dependency:go-offline

# copie o código
COPY src ./src

# empacota usando o mesmo settings
RUN --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml \
    mvn -s /root/.m2/settings.xml -B -V -DskipTests package