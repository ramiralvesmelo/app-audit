package br.com.ramiralvesmelo.audit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import br.com.ramiralvesmelo.util.security.KeycloakRoleConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// API REST Stateless: sem sessão/cookies (Não é possível CSRF - Cross-Site
				// Request Forgery)
				//.csrf(csrf -> csrf.disable()) 

				// Garante que NENHUMA sessão será criada (sem JSESSIONID/cookies)
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Boa prática: não expor formLogin/httpBasic/rememberMe em API
				.formLogin(form -> form.disable()).httpBasic(basic -> basic.disable()).rememberMe(rm -> rm.disable())
				.logout(lo -> lo.disable())

				// CORS se houver frontend em outro domínio
				.cors(Customizer.withDefaults())
				// health/info e endpoints públicos sem autenticação
				.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/actuator/health", "/actuator/info")
						.permitAll().requestMatchers("/api/public/**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.requestMatchers("/api/admin/**")
						// exige papel ADMIN (virá do JWT mapeado em GrantedAuthorities)
						.hasRole("ADMIN").anyRequest().authenticated())
				// Configura como Resource Server (espera Bearer JWT no Authorization)
				.oauth2ResourceServer(oauth2 -> oauth2
						// usa o JwtDecoder padrão (derivado do issuer-uri) e converte roles do Keycloak
						.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())));

		return http.build();
	}

	/**
	 * Converte as roles do Keycloak para o Spring (GrantedAuthorities): - Lê realm
	 * roles (realm_access.roles) e/ou client roles
	 * (resource_access["app-demo-api"].roles) - Prefixa como ROLE_*
	 * Assim, @PreAuthorize("hasRole('ADMIN')") e .hasRole("USER") funconam.
	 */
	private org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthConverter() {
		var converter = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
		// "app-demo-api" deve ser o clientId do recurso configurado no Keycloak
		converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter("app-demo-api"));
		return converter;
	}

}