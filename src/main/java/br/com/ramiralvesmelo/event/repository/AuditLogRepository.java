package br.com.ramiralvesmelo.event.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.ramiralvesmelo.event.document.AuditLogDoc;

public interface AuditLogRepository extends MongoRepository<AuditLogDoc, String> {
}
