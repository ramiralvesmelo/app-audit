package br.com.springboot.appdemo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import br.com.ramiralvesmelo.audit.config.KafkaTopicsConfig;

class KafkaTopicsConfigTest {

    @Test
    @DisplayName("Deve registrar o tópico orders.finalized com 1 partição e fator de réplica = 1")
    void shouldCreateOrdersFinalizedTopic() {
        try (var ctx = new AnnotationConfigApplicationContext(KafkaTopicsConfig.class)) {
            NewTopic topic = ctx.getBean(NewTopic.class);

            assertNotNull(topic, "Bean NewTopic não foi registrado");
            assertEquals("orders.finalized", topic.name());
            assertEquals(Integer.valueOf(1), topic.numPartitions());

            // Verifica fator de réplica (não o assignment)
            assertEquals(Short.valueOf((short) 1), topic.replicationFactor());

            // Opcional: deixa explícito que não há assignment definido
            assertNull(topic.replicasAssignments(), "replicasAssignments deve ser null quando usa replicationFactor");
        }
    }
}
