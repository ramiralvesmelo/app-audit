package br.com.ramiralvesmelo.event.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.com.ramiralvesmelo.event.document.AuditLogDoc;

class AuditLogDocTest {

    @Test
    void deveCriarAuditLogComIdEPayload() {
        AuditLogDoc doc = new AuditLogDoc();

        String id = "123";
        Map<String, Object> payload = new HashMap<>();
        payload.put("user", "ramir");
        payload.put("action", "CREATE");

        doc.setId(id);
        doc.setPayload(payload);

        assertThat(doc.getId()).isEqualTo(id);
        assertThat(doc.getPayload()).isNotNull();
        assertThat(doc.getPayload()).containsEntry("user", "ramir");
        assertThat(doc.getPayload()).containsEntry("action", "CREATE");
    }

    @Test
    void devePermitirPayloadVazio() {
        AuditLogDoc doc = new AuditLogDoc();
        doc.setId("456");
        doc.setPayload(new HashMap<>());

        assertThat(doc.getPayload()).isEmpty();
    }
}
