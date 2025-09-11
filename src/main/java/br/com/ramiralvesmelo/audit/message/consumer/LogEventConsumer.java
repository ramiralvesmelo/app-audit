package br.com.ramiralvesmelo.audit.message.consumer;

import java.util.HashMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.ramiralvesmelo.audit.document.AuditLogDoc;
import br.com.ramiralvesmelo.audit.repository.AuditLogRepository;
import br.com.ramiralvesmelo.util.message.event.AuditLogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogEventConsumer {

	private final AuditLogRepository repository;

	/**
	 * Escuta mensagens do tópico "orders.finalized". groupId garante que
	 * consumidores diferentes compartilham a carga.
	 */
	@KafkaListener(	topics = "${app.kafka.topic.audit-log-event}", 
					groupId = "${spring.kafka.consumer.group-id}")
	public void auditMessage(AuditLogEvent event) {
		log.info("Evento recebido → id={} payload={}", event.getId(), event.getPayload());
		AuditLogDoc doc = new AuditLogDoc();
		HashMap<String, Object> payload = new HashMap<>();
		payload.put("event", event);
		doc.setPayload(payload);
		repository.save(doc);
		log.info("Evento persistido no MongoDB (coleção audit_log_doc).");
	}
}