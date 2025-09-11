package br.com.ramiralvesmelo.audit.document;

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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

}
