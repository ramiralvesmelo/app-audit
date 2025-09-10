package br.com.ramiralvesmelo.audit.message.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.ramiralvesmelo.util.message.event.OrderFinalizedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderEventConsumer {

	/**
	 * Escuta mensagens do tópico "orders.finalized". groupId garante que
	 * consumidores diferentes compartilham a carga.
	 */
	@KafkaListener(topics = "${app.kafka.topic.order-finalized}", groupId = "${spring.kafka.consumer.group-id}")
	public void onOrderFinalized(OrderFinalizedEvent event) {
		log.info(
				"PEDIDO RECEBIDO PELO APP-AUDIT! recebido orderNumber={} total={} customerId={} topic={} groupId={} offset={}",
				event.getOrderNumber(), event.getTotalAmount(), event.getCustomerId(), "app.kafka.topic.order-finalized",
				"spring.kafka.consumer.group-id");
	}
}