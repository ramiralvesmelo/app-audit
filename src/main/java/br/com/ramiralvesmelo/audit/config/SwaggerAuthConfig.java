package br.com.ramiralvesmelo.audit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Classe de configuração responsável por integrar o Swagger (OpenAPI)
 * com o Keycloak utilizando OAuth2 (Authorization Code Flow).
 *
 * ✔️ Define os endpoints de autenticação e token do Keycloak (vindos do application.properties).
 * ✔️ Registra o esquema de segurança (OAuth2 + JWT) no Swagger.
 * ✔️ Aplica requisito de segurança global para enviar "Authorization: Bearer <token>".
 */
@Configuration
public class SwaggerAuthConfig {

    // Lidos do application.properties
    @Value("${springdoc.swagger-ui.oauth.auth-url}")
    private String authUrl;

    @Value("${springdoc.swagger-ui.oauth.token-url}")
    private String tokenUrl;

    @Bean
    OpenAPI openAPI() {

        // Define o fluxo OAuth2 do tipo "authorization code"
        var flows = new OAuthFlows().authorizationCode(
            new OAuthFlow()
                // URL de autorização (Keycloak) onde o usuário fará login
                .authorizationUrl(authUrl)
                // URL de troca de código por token (JWT) no Keycloak
                .tokenUrl(tokenUrl)
                // Escopos necessários para OIDC (ajuste se precisar de mais escopos/roles)
                .scopes(new Scopes().addString("openid", "OpenID Connect"))
        );

        // Esquema de segurança baseado em OAuth2 + JWT
        var scheme = new SecurityScheme()
            .name("keycloak")                        // Nome do esquema no Swagger
            .type(SecurityScheme.Type.OAUTH2)
            .bearerFormat("JWT")
            .flows(flows);

        // Registra o esquema e aplica segurança global
        return new OpenAPI()
            .components(new Components().addSecuritySchemes("keycloak", scheme))
            .addSecurityItem(new SecurityRequirement().addList("keycloak", "openid"));
    }
}
