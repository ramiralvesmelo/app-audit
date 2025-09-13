package br.com.ramiralvesmelo.event.message.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ramiralvesmelo.event.document.AuditLogDoc;
import br.com.ramiralvesmelo.event.message.consumer.OrderFinalizedEventConsumer;
import br.com.ramiralvesmelo.event.repository.AuditLogRepository;
import br.com.ramiralvesmelo.util.message.event.OrderFinalizedEvent;

@ExtendWith(MockitoExtension.class)
class OrderFinalizedEventConsumerTest {

    @Mock
    private AuditLogRepository repository;

    @InjectMocks
    private OrderFinalizedEventConsumer consumer;

    @Captor
    private ArgumentCaptor<AuditLogDoc> docCaptor;

    @Test
    void devePersistirEventoOrderFinalized() {
        // arrange
        OrderFinalizedEvent event = new OrderFinalizedEvent();
        event.setOrderNumber("ORD-123");
        event.setCustomerId(1L); // <- Long, conforme sua assinatura real
        event.setTotalAmount(new BigDecimal("250.75"));

        // act
        consumer.onOrderFinalized(event);

        // assert
        verify(repository, times(1)).save(docCaptor.capture());
        AuditLogDoc salvo = docCaptor.getValue();

        assertThat(salvo).isNotNull();
        assertThat(salvo.getPayload()).containsKey("event");
        assertThat(salvo.getPayload().get("event")).isSameAs(event);
    }
}
