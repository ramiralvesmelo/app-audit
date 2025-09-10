# syntax=docker/dockerfile:1.7
FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /app
SHELL ["/bin/bash", "-lc"]

COPY pom.xml ./

# 1) Gera settings.xml (placeholders)
RUN --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_pat \
    --mount=type=cache,target=/root/.m2/repository \
    export MAVEN_USERNAME="$(cat /run/secrets/gh_user)"; \
    export MAVEN_PASSWORD="$(cat /run/secrets/gh_pat)"; \
    mkdir -p /root/.m2; \
    cat <<'XML' | tr -d '\r' > /root/.m2/settings.xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github-ramir-util</id>
      <username>${MAVEN_USERNAME}</username>
      <password>${MAVEN_PASSWORD}</password>
    </server>
  </servers>
</settings>
XML

# 2) Baixa dependências (go-offline)
RUN --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_pat \
    --mount=type=cache,target=/root/.m2/repository \
    export MAVEN_USERNAME="$(cat /run/secrets/gh_user)"; \
    export MAVEN_PASSWORD="$(cat /run/secrets/gh_pat)"; \
    mvn -B -s /root/.m2/settings.xml -U dependency:go-offline

# 3) Compila
COPY src ./src
RUN --mount=type=secret,id=gh_user \
    --mount=type=secret,id=gh_pat \
    --mount=type=cache,target=/root/.m2/repository \
    export MAVEN_USERNAME="$(cat /run/secrets/gh_user)"; \
    export MAVEN_PASSWORD="$(cat /run/secrets/gh_pat)"; \
    mvn -B -s /root/.m2/settings.xml -DskipTests clean package

# ===== runtime =====
FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
