package br.com.ramiralvesmelo.event.message.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ramiralvesmelo.event.document.AuditLogDoc;
import br.com.ramiralvesmelo.event.message.consumer.LogEventConsumer;
import br.com.ramiralvesmelo.event.repository.AuditLogRepository;
import br.com.ramiralvesmelo.util.message.event.AuditLogEvent;

@ExtendWith(MockitoExtension.class)
class LogEventConsumerTest {

    @Mock
    private AuditLogRepository repository;

    @InjectMocks
    private LogEventConsumer consumer;

    @Captor
    private ArgumentCaptor<AuditLogDoc> docCaptor;

    @Test
    void devePersistirEventoNoMongo() {
        // arrange
        AuditLogEvent event = criarEvento("evt-123");

        // act
        consumer.auditMessage(event);

        // assert
        verify(repository, times(1)).save(docCaptor.capture());
        AuditLogDoc salvo = docCaptor.getValue();

        assertThat(salvo).isNotNull();
        assertThat(salvo.getPayload()).isNotNull();

        // comportamento atual do consumer: payload contém o objeto "event"
        assertThat(salvo.getPayload()).containsKey("event");
        assertThat(salvo.getPayload().get("event")).isEqualTo(event);
    }

    private AuditLogEvent criarEvento(String id) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "ORDER_FINALIZED");
        payload.put("orderId", "ORD-999");
        payload.put("amount", 123.45);

        AuditLogEvent e = new AuditLogEvent();
        e.setId(id);
        e.setPayload(payload);
        return e;
    }
}
