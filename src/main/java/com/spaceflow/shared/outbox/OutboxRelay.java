package com.spaceflow.shared.outbox;

import com.spaceflow.shared.event.BookingTopics;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Polls the outbox and relays messages to Kafka (at-least-once). A message is
 * flagged published only after the broker acks; on failure the transaction rolls
 * back and the message is retried on the next tick. Consumers must be idempotent.
 */
@Component
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxRelay(OutboxRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${spaceflow.outbox.poll-interval-ms:2000}")
    @Transactional
    public void relay() {
        List<OutboxMessage> batch = repository.findTop100ByPublishedFalseOrderByCreatedAtAsc();
        if (batch.isEmpty()) {
            return;
        }
        for (OutboxMessage message : batch) {
            try {
                kafkaTemplate.send(toRecord(message)).get();
                message.markPublished();
            } catch (Exception e) {
                // stop the batch; unpublished messages (incl. this one) retry next tick
                log.warn("Outbox relay failed for {}, will retry: {}", message.getId(), e.getMessage());
                break;
            }
        }
    }

    private ProducerRecord<String, String> toRecord(OutboxMessage message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                BookingTopics.BOOKING_EVENTS,
                message.getAggregateId().toString(),
                message.getPayload());
        record.headers().add(new RecordHeader("eventType",
                message.getEventType().getBytes(StandardCharsets.UTF_8)));
        record.headers().add(new RecordHeader("messageId",
                message.getId().toString().getBytes(StandardCharsets.UTF_8)));
        return record;
    }
}
