package br.com.ramiralvesmelo.audit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ramiralvesmelo.audit.document.AuditLogDoc;
import br.com.ramiralvesmelo.util.message.event.OrderFinalizedEvent;
import br.com.ramiralvesmelo.util.message.event.OrderFinalizedEvent.ItemDto;

@ActiveProfiles("test")
@Testcontainers
@DataMongoTest
class AuditLogOrderFinalizedRepositoryTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    private AuditLogRepository repository; // <-- nome do repositório correto

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void persistirEAoLerManterCamposDoOrderFinalizedEvent() {
        // Arrange
        OrderFinalizedEvent event = OrderFinalizedEvent.builder()
                .orderId(10L)
                .orderNumber("ORD-123")
                .customerId(99L)
                .totalAmount(new BigDecimal("199.80"))
                .occurredAt("2025-09-10T20:30:00Z")
                .message("Pedido finalizado com sucesso")
                .items(List.of(
                        ItemDto.builder()
                                .productId(1L).quantity(2)
                                .unitPrice(new BigDecimal("50.00"))
                                .subtotal(new BigDecimal("100.00"))
                                .build(),
                        ItemDto.builder()
                                .productId(2L).quantity(1)
                                .unitPrice(new BigDecimal("99.80"))
                                .subtotal(new BigDecimal("99.80"))
                                .build()))
                .build();

        // Converte para Map<String,Object> com TypeReference
        Map<String, Object> payload = mapper.convertValue(event, new TypeReference<Map<String, Object>>() {});

        AuditLogDoc doc = new AuditLogDoc();
        doc.setPayload(payload);

        // Act
        AuditLogDoc saved = repository.save(doc);
        var opt = repository.findById(saved.getId());

        // Assert
        assertThat(saved.getId()).isNotBlank();
        assertThat(opt).isPresent();

        Map<String, Object> lido = opt.get().getPayload();

        assertThat(lido.get("orderNumber")).isEqualTo("ORD-123");
        assertThat(((Number) lido.get("orderId")).longValue()).isEqualTo(10L);
        assertThat(((Number) lido.get("customerId")).longValue()).isEqualTo(99L);
        assertThat(lido.get("occurredAt")).isEqualTo("2025-09-10T20:30:00Z");
        assertThat(lido.get("message")).isEqualTo("Pedido finalizado com sucesso");
        assertThat(new BigDecimal(lido.get("totalAmount").toString()))
                .isEqualByComparingTo("199.80");

        List<?> items = (List<?>) lido.get("items");
        assertThat(items).hasSize(2);

        Map<?, ?> item1 = (Map<?, ?>) items.get(0);
        assertThat(((Number) item1.get("productId")).longValue()).isEqualTo(1L);
        assertThat(((Number) item1.get("quantity")).intValue()).isEqualTo(2);
        assertThat(new BigDecimal(item1.get("unitPrice").toString()))
                .isEqualByComparingTo("50.00");
        assertThat(new BigDecimal(item1.get("subtotal").toString()))
                .isEqualByComparingTo("100.00");

        Map<?, ?> item2 = (Map<?, ?>) items.get(1);
        assertThat(((Number) item2.get("productId")).longValue()).isEqualTo(2L);
        assertThat(((Number) item2.get("quantity")).intValue()).isEqualTo(1);
        assertThat(new BigDecimal(item2.get("unitPrice").toString()))
                .isEqualByComparingTo("99.80");
        assertThat(new BigDecimal(item2.get("subtotal").toString()))
                .isEqualByComparingTo("99.80");
    }

    @Test
    void atualizarParcialDoPayload() {
        Map<String, Object> payload = Map.of(
                "orderNumber", "UPD-001",
                "customerId", 10L,
                "totalAmount", new BigDecimal("50.00")
        );
        AuditLogDoc doc = new AuditLogDoc();
        doc.setPayload(payload);
        AuditLogDoc saved = repository.save(doc);

        var novo = new java.util.HashMap<>(saved.getPayload());
        novo.put("totalAmount", new BigDecimal("75.55"));
        novo.put("status", "UPDATED");
        saved.setPayload(novo);
        repository.save(saved);

        var atualizado = repository.findById(saved.getId());
        assertThat(atualizado).isPresent();
        var p = atualizado.get().getPayload();
        assertThat(new BigDecimal(p.get("totalAmount").toString())).isEqualByComparingTo("75.55");
        assertThat(p.get("status")).isEqualTo("UPDATED");
        assertThat(p.get("orderNumber")).isEqualTo("UPD-001");
    }

    @Test
    void substituirPayloadInteiro() {
        AuditLogDoc doc = new AuditLogDoc();
        doc.setPayload(Map.of("orderNumber", "REP-001", "totalAmount", new BigDecimal("10")));
        AuditLogDoc saved = repository.save(doc);

        Map<String,Object> novoPayload = Map.of(
                "orderNumber", "REP-001",
                "customerId", 777L,
                "comment", "payload substituído"
        );
        saved.setPayload(novoPayload);
        repository.save(saved);

        var found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getPayload().keySet()).containsExactlyInAnyOrder("orderNumber", "customerId", "comment");
        assertThat(((Number) found.getPayload().get("customerId")).longValue()).isEqualTo(777L);
    }

    @Test
    void listarTodosEContar() {
        repository.deleteAll();

        AuditLogDoc a = new AuditLogDoc();
        a.setPayload(Map.of("orderNumber", "A"));
        AuditLogDoc b = new AuditLogDoc();
        b.setPayload(Map.of("orderNumber", "B"));
        repository.saveAll(List.of(a, b));

        var all = repository.findAll();
        assertThat(all).hasSize(2);
        assertThat(repository.count()).isEqualTo(2);

        var orders = all.stream()
                .map(d -> String.valueOf(d.getPayload().get("orderNumber")))
                .toList();
        assertThat(orders).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void verificarExistenciaPorId() {
        AuditLogDoc doc = new AuditLogDoc();
        doc.setPayload(Map.of("orderNumber", "EXISTS-1"));
        AuditLogDoc saved = repository.save(doc);

        assertThat(repository.existsById(saved.getId())).isTrue();
        repository.deleteById(saved.getId());
        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void deletarPorId() {
        AuditLogDoc doc = new AuditLogDoc();
        doc.setPayload(Map.of("orderNumber", "DEL-1"));
        AuditLogDoc saved = repository.save(doc);

        repository.deleteById(saved.getId());
        assertThat(repository.findById(saved.getId())).isNotPresent();
    }

    @Test
    void deletarTodos() {
        repository.saveAll(List.of(newDoc("X"), newDoc("Y"), newDoc("Z")));
        assertThat(repository.count()).isGreaterThanOrEqualTo(3);

        repository.deleteAll();
        assertThat(repository.count()).isZero();
    }

    private static AuditLogDoc newDoc(String orderNumber) {
        AuditLogDoc d = new AuditLogDoc();
        d.setPayload(Map.of("orderNumber", orderNumber));
        return d;
    }
}
