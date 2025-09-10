package br.com.ramiralvesmelo.audit.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    NewTopic ordersFinalizedTopic() {
        return TopicBuilder.name("orders.finalized")
                .partitions(1)
                .replicas(1)
                .build();
    }
}