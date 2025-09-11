package br.com.ramiralvesmelo.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class ApplicationTests {


	@MockitoBean
	br.com.ramiralvesmelo.audit.message.consumer.LogEventConsumer orderEventConsumer;

	@Autowired
	private ApplicationContext context;

	@Test
	@DisplayName("Contexto deve subir no profile de teste")
	void contextLoads() {
		assertThat(context).isNotNull();
		// Garante que os beans problemáticos foram substituídos por mocks
		assertThat(orderEventConsumer).isNotNull();
		assertThat(Mockito.mockingDetails(orderEventConsumer).isMock()).isTrue();
	}

	@Test
	@DisplayName("Método main deve executar sem exceções")
	void mainMethodRuns() {
		assertDoesNotThrow(() -> Application.main(new String[] { 
				"--spring.profiles.active=test",
				"--spring.sql.init.mode=never", 
				"--spring.main.lazy-initialization=true" }));
	}
}
