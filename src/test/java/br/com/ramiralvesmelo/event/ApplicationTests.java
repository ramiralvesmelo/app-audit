package br.com.ramiralvesmelo.audit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class ApplicationTests {
	
	@MockitoBean
	br.com.ramiralvesmelo.audit.message.consumer.LogEventConsumer orderEventConsumer;	

    @Test
    void contextLoads() {
        // Se o contexto iniciar sem exceções, o teste passa ✅
    }
}
