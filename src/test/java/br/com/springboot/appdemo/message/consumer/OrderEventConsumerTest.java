package br.com.springboot.appdemo.message.consumer; 
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import br.com.ramiralvesmelo.audit.message.consumer.OrderEventConsumer;
import br.com.ramiralvesmelo.audit.message.event.OrderFinalizedEvent;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class OrderEventConsumerTest {

  @Test
  void deveRegistrarLogAoConsumirEvento() {
    Logger logger = (Logger) LoggerFactory.getLogger(OrderEventConsumer.class);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);

    OrderEventConsumer consumer = new OrderEventConsumer();
    OrderFinalizedEvent e = new OrderFinalizedEvent();
    e.setOrderNumber("ORD-123");
    e.setTotalAmount(new BigDecimal("99.90"));
    e.setCustomerId(1L);

    consumer.onOrderFinalized(e);

    var logs = appender.list.stream().map(ILoggingEvent::getFormattedMessage).collect(Collectors.toList());
    assertThat(logs).anySatisfy(msg -> {
      assertThat(msg).contains("PEDIDO RECEBIDO PELO APP-AUDIT!");
      assertThat(msg).contains("orderNumber=ORD-123");
      assertThat(msg).contains("total=99.90");
      assertThat(msg).contains("customerId=1");
    });

    logger.detachAppender(appender);
  }
}
