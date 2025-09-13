package br.com.ramiralvesmelo.event.document;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "audit_log_doc")
public class AuditLogDoc {
    @Id
    private String id;
    private Map<String, Object> payload;
}
