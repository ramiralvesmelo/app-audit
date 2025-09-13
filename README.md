# 📘 App Audit

[![Build Status](https://github.com/ramiralvesmelo/app-event/actions/workflows/maven.yml/badge.svg)](https://github.com/ramiralvesmelo/app-event/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.springframework.boot/spring-boot-starter.svg?logo=apache-maven)](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-21-blue.svg?logo=java)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Coverage Status](https://img.shields.io/codecov/c/github/ramiralvesmelo/app-event?logo=codecov)](https://app.codecov.io/gh/ramiralvesmelo/app-event)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ramiralvesmelo_app-event\&metric=alert_status)](https://sonarcloud.io/dashboard?id=ramiralvesmelo_app-event)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ramiralvesmelo_app-event\&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=ramiralvesmelo_app-event)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ramiralvesmelo_app-event\&metric=security_rating)](https://sonarcloud.io/dashboard?id=ramiralvesmelo_app-event)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ramiralvesmelo_app-event\&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=ramiralvesmelo_app-event)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

![Integração do App Audit](infra/img/app-demo-integration.drawio.png)

O **app-event** é uma aplicação de demonstração que simula um sistema **ERP (Enterprise Resource Planning)** em pequena escala, com módulos de:

* Cadastro de clientes
* Produtos e estoque
* Pedidos e vendas

![MER-001](infra/img/mer-001.png)

Objetivo: servir como **estudo de caso prático**, aplicando boas práticas de arquitetura, desenvolvimento e DevOps em um ambiente próximo ao real.

---

## 🗂️ Estrutura do Projeto

```text
app-event/
├── .github/                               		# Configurações do GitHub
│   └── workflows/                         		# Actions (CI)
│       └── maven.yml                      		# Pipeline Maven (build, testes, etc.)
├── infra/                                 		# Infra local e ferramentas
│   ├── docker/                            		# Docker / Compose da stack
│   │   ├── docker-compose.yml             		# Subir app + dependências (Postgres, Kafka, Redis, Keycloak)
│   │   └── Dockerfile                     		# Imagem da aplicação (JDK 21)
│   ├── insonia/                           		# Coleções do Insomnia
│   │   └── app-event-collection.yaml       		# Requests prontos (inclui auth)
│   ├── jmeter/                            		# Testes de carga/performance
│   │   └── post-customers-10000-random.jmx		# Script exemplo JMeter
│   └── keycloak/                          		# Realm e dados do Keycloak
│       └── realms/		
│           ├── app-event-realm.json        		# Realm com clients/roles/flows iniciais
│           └── h2/                        		# Base H2 do Keycloak (modo DEV)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/springboot/appdemo/ 		# Código-fonte principal
│   │   │       ├── Application.java       		# Classe bootstrap Spring Boot
│   │   │       ├── config/                		# Configurações (Security, Kafka, Transação, Web, etc.)
│   │   │       ├── controller/            		# REST Controllers
│   │   │       ├── exception/             		# Exceções de negócio e handler global
│   │   │       ├── message/               		# Eventos e integração (Kafka)
│   │   │       ├── model/                 		# DTOs e Entidades JPA
│   │   │       ├── repository/            		# Repositórios (interfaces + impl custom)
│   │   │       ├── service/               		# Interfaces e serviços (impl)
│   │   │       └── util/                  		# Utilitários (email, número de pedido, segurança)
│   │   └── resources/		
│   │       ├── application.properties     		# ⚙️ Config padrão (perfil default)
│   │       ├── application-docker.properties 	# ⚙️ Config para perfil `docker`
│   │       ├── schema.sql                 		# DDL inicial (dev/test)
│   │       └── data.sql                   		# Dados de exemplo (dev/test)
│   └── test/		
│       ├── java/                          		# Testes unitários/integração
│       └── resources/
│           └── application-test.properties		# Config de testes
├── .dockerignore
├── .gitignore
├── pom.xml                                		# Projeto Maven
└── README.md                              		# Este arquivo
```
---

## ⚙️ Pré-requisitos

* [Java 21+](https://adoptium.net/)
* [Maven 3.8+](https://maven.apache.org/)
* [Docker](https://www.docker.com/) MCP CLI v0.16.0 ou superior
* [Lombok](https://projectlombok.org/setup/) configurado na IDE
* [DBeaver](https://dbeaver.io/) (cliente universal para bancos de dados)
* [Eclipse STS (Spring Tool Suite)](https://spring.io/tools) (IDE otimizada para Spring)

---

# 🌐 URLs de Acesso

| Serviço           | URL / Endereço                                                                 | Usuário    | Senha     |
| ----------------- | ------------------------------------------------------------------------------ | ---------- | --------- |
| **App-demo**      | [http://localhost:8080](http://localhost:8080)                                 | -          | -         |
| **Swagger UI**    | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | -          | -         |
| **app-event**     | [http://localhost:8084](http://localhost:8084)                                 | -          | -         |
| **Keycloak**      | [http://localhost:8081](http://localhost:8081)                                 | admin      | admin     |
| **Healthcheck**   | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | -          | -         |
| **PostgreSQL**    | `jdbc:postgresql://localhost:5432/appdb`                                       | appuser    | apppass   |
| **H2**            | `jdbc:h2:mem:testdb`                                                           | sa         | -         |
| **H2 Console**    | [/h2-console](http://localhost:8080/h2-console)                                | sa         | -         |
| **Redis**         | `localhost:6379`                                                               | -          | -         |
| **Redis UI**      | [http://localhost:8082](http://localhost:8082)                                 | admin      | admin     |
| **Kafka**         | `localhost:9092`                                                               | -          | -         |
| **Kafka UI**      | [http://localhost:8083/ui/](http://localhost:8083/ui/)                         | -          | -         |
| **MongoDB**       | `mongodb://mongoadmin:mongopass@localhost:27017/auditdb?authSource=admin`      | mongoadmin | mongopass |
| **Mongo Express** | [http://localhost:8085](http://localhost:8085)                                 | admin      | admin     |
| **Zookeeper**     | `localhost:2181`                                                               | -          | -         |

---

### 🧪 Executar Testes Unitários com JaCoCo (Local)

💡 **Dica:** Para executar os testes com cobertura de código **localmente**, certifique-se de **habilitar a propriedade `jacocoArgLine`** no `pom.xml`.
Essa propriedade é preenchida automaticamente pelo **plugin JaCoCo** durante a execução da fase de testes.

```xml
<!-- pom.xml -->
<properties>
  <java.version>21</java.version>
  <maven.compiler.source>21</maven.compiler.source>
  <maven.compiler.target>21</maven.compiler.target>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

  <!-- ✅ Descomentar esse trecho para rodar testes de cobertura na ide do Eclipse (Necessário para o plugginJaCoCo) -->
  <jacocoArgLine></jacocoArgLine>
  ...
</properties>
```
---

## 🐳 Docker

```bash
# Build via Power Shell
$env:DOCKER_BUILDKIT = "1"
$env:MAVEN_USERNAME  = "ramiralvesmelo"
# Com permissão de leitura!
$env:MAVEN_PASSWORD  = "<GH_PACKAGES_TOKEN>"

docker buildx build `
  -f infra/docker/Dockerfile `
  --secret id=gh_user,env=MAVEN_USERNAME `
  --secret id=gh_pat,env=MAVEN_PASSWORD `
  -t app-event:1.0.0 .

# Executar container
docker run --rm -p 8080:8080 app-event:1.0.0

# Executar com config externa (mapeando a pasta local para /config no container)
docker run --rm -p 8080:8080 \
  -v $(pwd)/infra/docker/config:/config:ro \
  -e SPRING_CONFIG_LOCATION=file:/config/application.properties \
  app-event:1.0.0

# Windows PowerShell (ajuste o caminho conforme seu projeto):
docker run --rm -p 8080:8080 `
  -v ${PWD}\infra\docker\config:/config:ro `
  -e SPRING_CONFIG_LOCATION=file:/config/application.properties `
  app-event:1.0.0
```

---

## 🐳 Docker Compose

### Login no GHCR

```sh
# Substitua pelo seu PAT (token classic com read:packages)
$TOKEN = "<GH_PACKAGES_TOKEN>"
$TOKEN | docker login ghcr.io -u ramiralvesmelo --password-stdin
```

### Subir todos os serviços
```bash
docker compose -f infra/docker/docker-compose.yml up -d --build
```

### Subir os serviços: Kafka + KafkaUI + Zookeeper + Keycloak + PostgreSQL + Redis + Redis Commmander + MongoDB + Mongo Express
```bash
docker compose -f infra/docker/docker-compose.yml up -d zookeeper kafka keycloak redis postgres kafka-ui redis-commander mongodb mongo-express minio
```

### Logs do serviço app-event
```bash
docker compose -f infra/docker/docker-compose.yml logs -f app-event
```

### Derrubar Todos os serviços
```bash
docker compose -f infra/docker/docker-compose.yml down
```

```bash
# (opcional) removendo volumes:
docker compose -f infra/docker/docker-compose.yml down -v
```

---

## 📨 Kafka

###

* **`app.kafka.topic.order-finalized`** → Nome do **tópico Kafka** onde serão publicadas as mensagens de pedidos finalizados.
  Exemplo: sempre que um pedido é concluído, uma mensagem é enviada para esse tópico.

* **`spring.kafka.consumer.group-id`** → Identificador do **grupo de consumidores**.
  Todos os consumidores com o mesmo `group-id` compartilham a carga das mensagens do tópico.
  Isso garante **paralelismo** e **balanceamento** — cada mensagem é entregue para apenas um consumidor dentro do grupo.

### 🌐 Interface de Visualização

* **Kafka UI:** [http://localhost:8083/ui/](http://localhost:8083/ui/)

Acesse para visualizar:

* 📋 Lista de tópicos disponíveis
* 📦 Mensagens enviadas e recebidas
* 👥 Grupos de consumidores ativos e seus offsets



---

## 🔑 Keycloak

### 📍 Acesso pelo Host (Windows/Linux)

Para acessar o **Keycloak** pelo **nome do serviço** `keycloak` a partir do **host**, adicione a entrada no arquivo *hosts* do sistema:

```text
127.0.0.1   keycloak
```

**Caminhos dos arquivos de hosts:**

* 🪟 **Windows:** `C:\Windows\System32\drivers\etc\hosts`
* 🐧 **Linux:** `/etc/hosts`

> 📌 Observação: dentro da **rede do Docker Compose**, o DNS já resolve `keycloak`. O ajuste acima é apenas para o **host** conseguir acessar `http://keycloak:8081/` (útil quando o *issuer* do token ou a documentação referem-se a `keycloak:8081`).

---

### ➡️ Console de Administração

* **URL:** [http://keycloak:8081/](http://keycloak:8081/)
* **Usuário:** `admin` *(ou o valor definido em `KEYCLOAK_ADMIN`)*
* **Senha:** `admin` *(ou o valor definido em `KEYCLOAK_ADMIN_PASSWORD`)*

**Validação da aplicação:**

* **URL:** [http://keycloak:8080/actuator/health](http://keycloak:8080/actuator/health)

> 💡 **Observação:** arquivo `app-event-realm.json` contém as configurações iniciais do realm.

---

## 🚀 Fluxo Simplificado (Authorization Code + PKCE)

![Integração do App Audit](infra/img/auth-code-flow.png)

1. **Usuário clica em “Login” no App**
   O App começa o processo de autenticação.

2. **App gera `code_verifier` e `code_challenge`**
   O `code_verifier` é secreto, fica só no App.
   O `code_challenge` (hash do verifier) vai para o servidor.

3. **App redireciona usuário ao Authorization Server (Keycloak)**
   Envia junto: `Authorization Code`: {`client_id`, `redirect_uri`, `scope`} + `code_challenge`.
   O usuário vê a tela de login.

4. **Usuário autentica no Authorization Server**
   Digita login/senha (ou certificado, MFA).
   Se aprovado, o servidor gera um **Authorization Code** temporário.

5. **Authorization Server redireciona de volta para o App**
   O `authorization_code` é enviado para o `redirect_uri`.

6. **App troca o Authorization Code por Tokens**
   Faz uma chamada ao servidor com: `client_id`, `redirect_uri`, `authorization_code` e **`code_verifier`**.

7. **Authorization Server valida**
   Recalcula o hash do `code_verifier` e compara com o `code_challenge`.
   Se bater, devolve **Access Token (JWT)** (+ ID Token e Refresh Token se configurado).

8. **App usa o Access Token para chamar a API**
   `Authorization: Bearer <token>`
   A API valida e responde com os dados.

---

ℹ️ **Nota Importante:**

* **Sem PKCE:** `client_secret` guardado na aplicação (confidential client).
* **Com PKCE:** sem `client_secret`, segurança garantida pelo `code_verifier`/`code_challenge` (public client).

---

### 📖 Authorization Code + PKCE (como configura no Keycloak)

Este guia descreve como configurar um **Client** no Keycloak para utilizar o fluxo **Authorization Code + PKCE (S256)**, e como integrar com o **Spring Boot (springdoc + Swagger UI)**.


---

#### 🔧 Passo 1: Criar ou editar o Client

1. Vá em **Clients → app-event-api** (ou crie um novo com este Client ID).
2. Configure:

   * **Client authentication**: **Off** (cliente público)
   * **Authorization**: **Off**
   * **Standard flow**: ✅ **On** (obrigatório)
   * **Direct access grants**: ❌ Off
   * **Implicit flow**: ❌ Off
   * **Service accounts**: ❌ Off

---

#### 🌐 Passo 2: URIs e CORS

* **Valid Redirect URIs**:

  ```
  http://localhost:8080/swagger-ui/oauth2-redirect.html
  ```
* **Web origins**:

  ```
  http://localhost:8080
  ```

---

#### 🔒 Passo 3: Ativar PKCE (S256)


Na aba **Advanced settings**:

* **Proof Key for Code Exchange Code Challenge Method**: **S256**

---

#### ⚙️ Passo 4: Configuração no Spring Boot

##### application.properties

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/app-event

# Swagger UI OAuth (springdoc)
springdoc.swagger-ui.oauth.client-id=app-event-api
springdoc.swagger-ui.oauth.use-pkce-with-authorization-code-grant=true
springdoc.swagger-ui.oauth.scopes=openid,profile
springdoc.swagger-ui.oauth2-redirect-url=http://localhost:8080/swagger-ui/oauth2-redirect.html
springdoc.swagger-ui.oauth.authorization-url=http://localhost:8081/realms/app-event/protocol/openid-connect/auth
springdoc.swagger-ui.oauth.token-url=http://localhost:8081/realms/app-event/protocol/openid-connect/token
```




#### 🧪 Passo 5: Testar no Swagger UI

1. Acesse **[http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)**
2. Clique em **Authorize**
3. Faça login no Keycloak
4. O cadeado ficará verde ✅ e as requisições terão **Bearer Token** automaticamente

---

#### 📦 Capturar TOKEN no Insomnia

O guia abaixo mostra como:

* Capturar o `access_token` no corpo da resposta (JSON)
* Salvar em uma variável de ambiente `TOKEN`
* Usar automaticamente como **Bearer** nas requisições da API

<table>
  <tr>
    <td valign="top"><img src="infra/img/insonia-001.png" alt="Insonia-001" width="500"/></td>
    <td valign="top"><img src="infra/img/insonia-002.png" alt="Insonia-002" width="500"/></td>
  </tr>
</table>

---

👉 [Baixar `app-event-collection.yaml`](./infra/insonia/app-event-collection.yaml)

**Como importar:**

1. Abra o **Postman** ou **Insomnia**
2. Clique em **Import > File**
3. Selecione o arquivo `app-event-collection.yaml`

> 💡 Na **collection do Insonia** já existe a requisição pronta para obter o token.
> O passo a passo manual abaixo é útil para entender e testar via **curl**.

---

### 🖥️ Exemplo via **cURL** (Linux/macOS)

```sh
export TOKEN=$(curl -s \
  -d "client_id=app-event-api" \
  -d "username=appdemo" \
  -d "password=123" \
  -d "grant_type=password" \
  http://keycloak:8081/realms/app-event/protocol/openid-connect/token | jq -r .access_token)

echo $TOKEN

# Teste de requisição (resposta 200 se autenticado)
curl -s -H "Authorization: Bearer $TOKEN" http://keycloak:8080/api/products
```

### 🖥️ Exemplo via **PowerShell** (Windows)

```powershell
$body = @{
  client_id  = "app-event-api"
  username   = "appdemo"
  password   = "123"
  grant_type = "password"
  # Se o client for confidential:
  # client_secret = "<SEU_CLIENT_SECRET>"
  # Se exigir 2FA:
  # totp = "123456"
}

$TOKEN = (Invoke-RestMethod -Method Post `
  -Uri "http://keycloak:8081/realms/app-event/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body $body).access_token

# Teste de requisição (resposta 200 se autenticado)
Invoke-RestMethod -Method Get `
  -Uri "http://keycloak:8080/api/products" `
  -Headers @{ Authorization = "Bearer $TOKEN" }
```

---

## 📊 JMeter – Testes de Carga

```sh
# Linux
rm -rf /temp/jmeter/
mkdir -p /temp/jmeter/

# Windows
Remove-Item -Recurse -Force "/temp/jmeter"
New-Item -ItemType Directory -Path "/temp/jmeter"

# Executar plano de teste
jmeter -n -t post-customers-10000-random.jmx \
  -l /temp/jmeter/results.jtl \
  -e -o /temp/jmeter/report
```
---

## 🔄 Integração Contínua (CI/CD)

Este projeto conta com um pipeline de **CI/CD** definido no workflow **Executar GitAction** em `.github/workflows`.

### Workflows

* ⚙️ **Build** → Compila e empacota o projeto (Maven).
* 🧪 **Testes** → Executa testes unitários e de integração (JUnit 5 + Mockito).
* 🩺 **Check Health** → Verifica a saúde do projeto com **SonarCloud** e **Codecov**.
* 📦 **Publicação** → Publica o artefato no GitHub Packages e gera imagem Docker (GHCR + DockerHub).

### Estrutura do Workflow

* 🔍 **CI → Análise de Código & Cobertura**

  * Configura JDK 24 (Temurin) e cache Maven.
  * Build + Test + Coverage com **Jacoco**.
  * Envio de relatórios de cobertura para **Codecov**.
  * Análise estática e Quality Gate via **SonarCloud**.
  * Geração de **SBOM (CycloneDX)**.
  * Submissão de snapshot de dependências para **Dependency Graph**.

* 🚀 **CD → Imagem Docker & Deploy**

  * Build multi-plataforma com Docker Buildx.
  * Publicação da imagem no **GitHub Container Registry (GHCR)** e **DockerHub**.
  * Configuração de `maven-settings.xml` com credenciais seguras.
  * Tags automáticas para imagens Docker (latest, branch, tag, SHA).

### Integrações principais

* 📊 **Codecov** → Identificação de trechos sem cobertura de testes.
* ☁️ **SonarCloud** → Análise contínua de bugs, vulnerabilidades e duplicidade.
* 📦 **GitHub Packages** → Reuso e versionamento de artefatos.
* 🐳 **GHCR/DockerHub** → Distribuição de imagens Docker do projeto.
* 📈 **Dependency Graph** → Detecção de riscos em bibliotecas externas.
* 📄 **SBOM (CycloneDX)** → Inventário de componentes para auditoria e segurança.
* 🚨 **Dependabot Alerts** → Notificação de vulnerabilidades em dependências.
* 🏷️ **Badges no README** → Indicadores visuais de saúde e qualidade do projeto.

---

## 🔐 Configuração de Tokens (GitHub | SonarCloud | Codecov | DockerHub) no GitAction

### ### 🔑 Configuração de Secrets no GitHub

Para configurar os tokens de acesso no GitHub, vá até:
**Project → Settings → Security → Secrets and variables → Actions → New repository secret**

Crie os seguintes secrets:

| Secret               | Descrição                                               | Onde gerar                                                                  |
| -------------------- | ------------------------------------------------------- | --------------------------------------------------------------------------- |
| `GH_PACKAGES_TOKEN`  | Token do GitHub com permissão **read**\*\*:packages\*\* | *Settings > Developer Settings > Personal access tokens > Tokens (classic)* |
| `SONAR_TOKEN`        | Token do SonarCloud com permissão **Execute Analysis**  | *My Account > Security > Tokens*                                            |
| `CODECOV_TOKEN`      | Token do Codecov para envio de relatórios de cobertura  | *My Account > Settings > Access > Generate Token*                           |
| `DOCKERHUB_TOKEN`    | Token de acesso ao DockerHub                            | *My Account Settings > Personal access tokens > Generate new token*         |
| `DOCKERHUB_USERNAME` | Usuário do DockerHub vinculado ao token                 | *Conta DockerHub*                                                           |

---

### ⚙️ 2. Ajustar Método de Análise no SonarCloud

No painel do projeto (canto superior direito) :

No SonarCloud acesse: **Project Name → Administration → Analysis Method**

* **Desativar**: *Automatic Analysis (default)*
* **Ativar**: *CI-based Analysis* (**GitHub Actions, Maven, etc.**)

> Assim o SonarCloud não roda análise duplicada e o pipeline passa a controlar a execução com cobertura de testes e Quality Gate.

---

## 🌱 Fluxo de Branches (GitFlow)

Adotamos o **GitFlow** para organizar entregas e paralelizar trabalho com segurança:

* **`main`**: linha de produção (somente `releases` e `hotfixes` versionados).
* **`develop`**: linha de desenvolvimento contínuo (base para `features`).
* **`feature/*`**: novas funcionalidades ou melhorias curtas, criadas a partir de `develop`.
* **`release/*`**: preparação de versão; estabilização e ajustes finais, criada a partir de `develop`.
* **`hotfix/*`**: correções urgentes em produção, criadas a partir de `main` e integradas de volta em `main` e `develop`.

---

### 🗺️ Gráfico (GitFlow)

```mermaid
gitGraph
   commit id: "v0.1"
   branch develop
   commit
   branch feature/featureA
   commit
   commit
   checkout develop
   merge feature/featureA
   branch feature/featureB
   commit
   commit
   checkout develop
   merge feature/featureB
   branch release/0.2
   commit
   commit
   checkout main
   merge release/0.2 id: "v0.2"
   checkout develop
   merge release/0.2
   branch hotfix/0.2.1
   commit
   checkout main
   merge hotfix/0.2.1 id: "v0.2.1"
   checkout develop
   merge hotfix/0.2.1
   branch release/1.0
   commit
   commit
   checkout main
   merge release/1.0 id: "v1.0"
   checkout develop
   merge release/1.0
```

---

### 📌 Legenda

| Branch       | Função                                                                        |
| ------------ | ----------------------------------------------------------------------------- |
| **main**     | Produção, recebe merges de `release` e `hotfix` com **tags** de versão        |
| **develop**  | Desenvolvimento contínuo, recebe merges de `features`, `release` e `hotfix`   |
| **feature/** | Desenvolvimento de novas funcionalidades a partir de `develop`                |
| **release/** | Preparação de versões, testes e ajustes finais antes de ir para `main`        |
| **hotfix/**  | Correções urgentes criadas a partir de `main`, voltam para `main` e `develop` |

---

## 📜 Licença

Distribuído sob a licença **MIT**. &#x20;
Sinta-se livre para usar, modificar e compartilhar.