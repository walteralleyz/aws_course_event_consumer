package br.walleyz.aws_course_p2.service;

import br.walleyz.aws_course_p2.model.Envelope;
import br.walleyz.aws_course_p2.model.ProductEvent;
import br.walleyz.aws_course_p2.model.ProductEventLog;
import br.walleyz.aws_course_p2.model.SnsMessage;
import br.walleyz.aws_course_p2.repository.ProductEventLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.Duration;
import java.time.Instant;

@Service
public class ProductEventConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(ProductEventConsumer.class);

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ProductEventLogRepository productEventLogRepository;

    @JmsListener(destination = "${aws.sqs.queue.product.events.name}")
    public void receiveProductEvent(TextMessage textMessage) throws JMSException, JsonProcessingException {
        SnsMessage message = mapper.readValue(textMessage.getText(), SnsMessage.class);
        Envelope envelope  = mapper.readValue(message.getMessage(), Envelope.class);
        ProductEvent event = mapper.readValue(envelope.getData(), ProductEvent.class);

        LOG.info("Message received with Id: {}", message.getMessageId());

        LOG.info("Product Event received - Event: {} - ProductId: {}",
            envelope.getEventType(),
            event.getProductId());

        ProductEventLog eventLog = buildProductEventLog(envelope, event);
        productEventLogRepository.save(eventLog);
    }

    private ProductEventLog buildProductEventLog(Envelope envelope, ProductEvent productEvent) {
        long timestamp = Instant.now().toEpochMilli();

        ProductEventLog eventLog = new ProductEventLog();
        eventLog.setPk(productEvent.getCode());
        eventLog.setSk(envelope.getEventType() + "_" + timestamp);
        eventLog.setEventType(envelope.getEventType());
        eventLog.setProductId(productEvent.getProductId());
        eventLog.setUsername(productEvent.getUsername());
        eventLog.setTimestamp(timestamp);
        eventLog.setTtl(Instant.now().plus(Duration.ofMinutes(10)).getEpochSecond());

        return eventLog;
    }
}
