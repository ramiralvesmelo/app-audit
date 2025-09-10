package br.com.springboot.appdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.com.ramiralvesmelo.audit.Application;

/**
 * Teste de integração simples para garantir que o contexto da aplicação Spring Boot
 * seja carregado corretamente sem falhas de configuração.
 *
 * ✅ Passa se todas as configurações, beans e dependências forem inicializados com sucesso.
 * ⚠️ Falha se houver erro de configuração no Spring, no JPA ou em qualquer bean obrigatório.
 */
@SpringBootTest(classes = Application.class)
@Import(SimpleIntegrationTestConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "logging.level.org.springframework=DEBUG",
    "logging.level.org.hibernate=DEBUG",
    "spring.jpa.show-sql=true"
})
public class SimpleIntegrationTest {

	@MockitoBean
    br.com.ramiralvesmelo.audit.message.consumer.OrderEventConsumer orderEventConsumer;	

    @Test
    public void contextLoads() {
        // This test will pass if the application context loads successfully
    }
}
