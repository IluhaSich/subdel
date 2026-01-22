package com.example.subdel.listeners;

import com.example.events_contract.events.DelicacyAnalyzedEvent;
import com.example.events_contract.events.DelicacyCreatedEvent;
import com.example.events_contract.events.DelicacyDeletedEvent;
import com.example.events_contract.events.ProductDto;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DelicacyEventListener {
    private static final Logger log = LoggerFactory.getLogger(DelicacyEventListener.class);
    private static final String EXCHANGE_NAME = "delicacy-exchange";
    private static final String QUEUE_NAME = "notification-queue";


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME,
                            durable = "true",
                            // если что-то пойдет не так, отправляем в 'dlx-exchange'
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "delicacy.created"
            )
    )
    public void handleDelicacyCreatedEvent(@Payload DelicacyCreatedEvent event, Channel channel,
                                           @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received DelicacyCreatedEvent: {}", event);
            if (event.delicacyId() != null
                    && event.products().stream()
                    .anyMatch(productDto ->
                            productDto.name().equalsIgnoreCase("CRASH"))) {
                throw new RuntimeException("Simulating processing error for DLQ test");
            }
            //TODO: обратно сообщения для изменения price
            // TODO: Также добавить дополнительную стоимость
            log.info("Delicacy price is: {}.",
                event.products().stream().mapToDouble(ProductDto::price).sum());

            // Логика отправки уведомления...
            log.info("Notification sent for new delicacy '{}'!", event.delicacyId());
            // Отправляем подтверждение брокеру
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            // Отправляем nack и НЕ просим вернуть в очередь (requeue=false)
            channel.basicNack(deliveryTag, false, false);
        }
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "delicacy.deleted"
            )
    )
    public void handleBookDeletedEvent(@Payload DelicacyDeletedEvent event, Channel channel,
                                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.info("Received BookDeletedEvent: {}", event);
            // Логика отмены уведомлений...
            log.info("Notifications cancelled for deleted bookId {}!", event.delicacyId());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "notification-queue.dlq", durable = "true"),
                    exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
                    key = "dlq.notifications"
            )
    )
    public void handleDlqMessages(Object failedMessage) {
        log.error("!!! Received message in DLQ: {}", failedMessage);
        // Здесь может быть логика оповещения администраторов
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "q.subdel.delicacy.analytics", durable = "true"),
                    exchange = @Exchange(name = "delicacy-analytics-fanout", type = "fanout")
            )
    )
    public void handleAnalytics(DelicacyAnalyzedEvent event) {
        log.info(
                "Subdel: Delicacy {} analyzed. Score={}, Verdict={}",
                event.delicacyId(),
                event.score(),
                event.verdict()
        );
    }

}
