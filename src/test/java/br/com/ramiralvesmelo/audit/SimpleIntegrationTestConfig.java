package br.com.ramiralvesmelo.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

/**
 * Configuração personalizada de contexto para execução de testes de integração.
 *
 * 🔹 Ativada apenas no perfil {@code test}.
 * 🔹 Utiliza banco de dados H2 em memória, criado e destruído a cada execução de teste.
 * 🔹 Configura o EntityManagerFactory, TransactionManager e integração com JPA/Hibernate.
 *
 * Principais pontos:
 * - DataSource: banco H2 em memória, rápido e isolado para cada execução.
 * - EntityManagerFactory: escaneia as entidades do pacote {@code br.com.springboot.appdemo.model}.
 * - TransactionManager: gerencia transações JPA nos testes.
 * - Propriedades extras: dialeto do Hibernate ajustado para H2, criação e remoção automática de tabelas.
 *
 * ✅ Benefício: garante que os testes rodem de forma independente,
 * sem depender de banco externo ou configuração manual.
 */
@TestConfiguration
@Profile("test")
@EnableTransactionManagement
@ComponentScan(basePackages = {
    "br.com.springboot.appdemo.repository",
    "br.com.springboot.appdemo.service",
    "br.com.springboot.appdemo.controller"
})
public class SimpleIntegrationTestConfig {
	
    @Bean
    @Primary // instancia antes do auto-config
    public JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token == null ? "test-token" : token)
            .headers(h -> h.put("alg", "none"))
            .claim("sub", "tester")
            // simula roles de realm do Keycloak → realm_access.roles
            .claim("realm_access", Map.of("roles", Set.of("ADMIN","USER")))
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .issuer("http://localhost/test-issuer")
            .build();
    }	

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .driverClassName("org.h2.Driver")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("br.com.springboot.appdemo.model");
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        return adapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }
}
