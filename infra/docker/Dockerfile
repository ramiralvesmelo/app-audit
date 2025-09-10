# syntax=docker/dockerfile:1.4

##############################
# ===== STAGE 1: BUILD ===== #
##############################
FROM maven:3.9.9-eclipse-temurin-24 AS build
SHELL ["/bin/bash", "-lc"]
WORKDIR /app

# Copia apenas o pom para otimizar cache de dependências
COPY pom.xml .

# Baixa dependências usando secrets (GitHub Packages) e cache do ~/.m2
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_pat <<BASH
set -eu
U=$(cat /run/secrets/gh_user)
P=$(cat /run/secrets/gh_pat)

install -d -m 700 /root/.m2

# Gera settings.xml temporário; o <server><id> deve bater com o <repository id="github-ramir-util"> no POM
cat > /root/.m2/settings.xml <<XML
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github-ramir-util</id>
      <username>${U}</username>
      <password>${P}</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <id>repos</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>false</enabled></snapshots>
        </repository>
        <repository>
          <id>github-ramir-util</id>
          <url>https://maven.pkg.github.com/ramiralvesmelo/util</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>repos</activeProfile>
  </activeProfiles>
</settings>
XML

mvn -s /root/.m2/settings.xml -B -V -U -ntp dependency:go-offline
shred -u /root/.m2/settings.xml
BASH

# Copia o código-fonte
COPY src ./src

# Empacota o app (gera o JAR)
RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_pat <<BASH
set -eu
U=$(cat /run/secrets/gh_user)
P=$(cat /run/secrets/gh_pat)

install -d -m 700 /root/.m2
cat > /root/.m2/settings.xml <<XML
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
  <servers>
    <server>
      <id>github-ramir-util</id>
      <username>${U}</username>
      <password>${P}</password>
    </server>
  </servers>
</settings>
XML

mvn -s /root/.m2/settings.xml -B -V -ntp -DskipTests package
shred -u /root/.m2/settings.xml
BASH


################################
# ===== STAGE 2: RUNTIME ===== #
################################
FROM eclipse-temurin:24-jre
WORKDIR /app

# Usuário não-root
RUN addgroup --system app && adduser --system --ingroup app app

# Copia o JAR (ajuste o padrão se necessário)
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
USER app

ENTRYPOINT ["bash","-lc","java $JAVA_OPTS -jar /app/app.jar"]