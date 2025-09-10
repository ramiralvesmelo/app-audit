# syntax=docker/dockerfile:1.4

##############################
# ===== STAGE 1: BUILD ===== #
##############################
FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /app

# Copia só o POM p/ maximizar cache de deps
COPY pom.xml .

# Baixa dependências (usa secrets para GitHub Packages)
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_pat \
    set -eu; \
    U="$(cat /run/secrets/gh_user)"; \
    P="$(cat /run/secrets/gh_pat)"; \
    install -d -m 700 /root/.m2; \
    printf '%s\n' \
'<?xml version="1.0" encoding="UTF-8"?>' \
'<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">' \
'  <servers>' \
'    <server>' \
'      <id>github-ramir-util</id>' \
"      <username>${U}</username>" \
"      <password>${P}</password>" \
'    </server>' \
'  </servers>' \
'  <profiles>' \
'    <profile>' \
'      <id>repos</id>' \
'      <repositories>' \
'        <repository><id>central</id><url>https://repo1.maven.org/maven2</url><releases><enabled>true</enabled></releases><snapshots><enabled>false</enabled></snapshots></repository>' \
'        <repository><id>github-ramir-util</id><url>https://maven.pkg.github.com/ramiralvesmelo/util</url><releases><enabled>true</enabled></releases><snapshots><enabled>true</enabled></snapshots></repository>' \
'      </repositories>' \
'    </profile>' \
'  </profiles>' \
'  <activeProfiles><activeProfile>repos</activeProfile></activeProfiles>' \
'</settings>' > /root/.m2/settings.xml; \
    mvn -s /root/.m2/settings.xml -B -V -U -ntp dependency:go-offline; \
    rm -f /root/.m2/settings.xml

# Copia o código fonte
COPY src ./src

# Empacota o app (gera o JAR)
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_pat \
    set -eu; \
    U="$(cat /run/secrets/gh_user)"; \
    P="$(cat /run/secrets/gh_pat)"; \
    install -d -m 700 /root/.m2; \
    printf '%s\n' \
'<?xml version="1.0" encoding="UTF-8"?>' \
'<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">' \
'  <servers>' \
'    <server>' \
'      <id>github-ramir-util</id>' \
"      <username>${U}</username>" \
"      <password>${P}</password>" \
'    </server>' \
'  </servers>' \
'</settings>' > /root/.m2/settings.xml; \
    mvn -s /root/.m2/settings.xml -B -V -ntp -DskipTests package; \
    rm -f /root/.m2/settings.xml

################################
# ===== STAGE 2: RUNTIME ===== #
################################
FROM eclipse-temurin:24-jre
WORKDIR /app

# Usuário não-root
RUN addgroup --system app && adduser --system --ingroup app app

# Copia o JAR (ajuste o nome se necessário)
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
USER app

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]