package br.com.ramiralvesmelo.audit.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.ramiralvesmelo.audit.document.AuditLogDoc;

public interface AuditLogRepository extends MongoRepository<AuditLogDoc, String> {
}
